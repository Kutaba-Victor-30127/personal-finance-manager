import api from "./api";
import type { LoginRequest, AuthResponse, MessageResponse} from "../types/auth";

export const login = async (
  credentials: LoginRequest
): Promise<AuthResponse> => {

  const response = await api.post<AuthResponse>(
    "/auth/login",
    credentials
  );

  return response.data;
};

export const logout = async (
  refreshToken: string
): Promise<MessageResponse> => {

  const response = await api.post<MessageResponse>(
    "/auth/logout",
    { refreshToken }
  );
  
  return response.data;

}
