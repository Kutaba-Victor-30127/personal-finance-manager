import axios, {
  type AxiosError,
  type InternalAxiosRequestConfig,
} from "axios";
import {
  getAccessToken,
  getRefreshToken,
  saveTokens,
  clearTokens,
} from "../utils/tokenStorage";

import type { AuthResponse } from "../types/auth";

type RetryRequestConfig = InternalAxiosRequestConfig & {
  _retry?: boolean;
};

let refreshPromise: Promise<AuthResponse> | null = null;

const api = axios.create({
  baseURL: "http://localhost:8080/api",
  headers: {
    "Content-Type": "application/json",
  },
});

const refreshApi = axios.create({
  baseURL: "http://localhost:8080/api",

  headers: {
    "Content-Type": "application/json",
  },
});

const isAuthRequest = (url?: string) =>
  url?.startsWith("/auth/login") ||
  url?.startsWith("/auth/register") ||
  url?.startsWith("/auth/refresh") ||
  url?.startsWith("/auth/logout");

const redirectToLogin = () => {
  clearTokens();

  if (window.location.pathname !== "/login") {
    window.location.href = "/login";
  }
};

api.interceptors.request.use(
  (config) => {
    const accessToken = getAccessToken();

    if (accessToken && !isAuthRequest(config.url)) {
      config.headers["Authorization"] = `Bearer ${accessToken}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

const refreshTokens = async () => {

  const refreshToken = getRefreshToken();

  if (!refreshToken) {
    throw new Error("Refresh token not found");
  }

  const response = await refreshApi.post<AuthResponse>(
    "/auth/refresh", {
      refreshToken: refreshToken,
    }
  );

  return response.data;
};

const refreshTokensOnce = () => {
  if (!refreshPromise) {
    refreshPromise = refreshTokens()
      .then((tokens) => {
        saveTokens(tokens.accessToken, tokens.refreshToken);

        return tokens;
      })
      .finally(() => {
        refreshPromise = null;
      });
  }

  return refreshPromise;
};

api.interceptors.response.use(
  (response) => {
    return response;
  },

  async (error: AxiosError) => {

    const originalRequest = error.config as RetryRequestConfig | undefined;

    if(!originalRequest) {
      return Promise.reject(error);
    }

    if (
      error.response?.status !== 401 ||
      originalRequest._retry ||
      isAuthRequest(originalRequest.url)
    ) {
      return Promise.reject(error);
    }

    originalRequest._retry = true;

    try {

      const tokens = await refreshTokensOnce();

      originalRequest.headers["Authorization"] = `Bearer ${tokens.accessToken}`;

      return api(originalRequest);
    } catch (refreshError) {
      redirectToLogin();

      return Promise.reject(refreshError);
    }
  }
);

export default api;
