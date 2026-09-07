import { Navigate } from "react-router-dom";
import { getAccessToken } from "../utils/tokenStorage";

interface ProtectedRouteProps {
  children: React.ReactNode;
}

export default function ProtectedRoute({
     children, 
}: ProtectedRouteProps) {

    const accessToken = getAccessToken();

    if (!accessToken) {
        return <Navigate to="/login" replace />;
    }

    return <>{children}</>;
}