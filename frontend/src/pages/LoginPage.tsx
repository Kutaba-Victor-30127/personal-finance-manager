import {Link} from 'react-router-dom';
import { z } from 'zod';
import { zodResolver} from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { login } from '../services/authService';
import { useNavigate } from 'react-router-dom';
import { saveTokens } from '../utils/tokenStorage';

const loginSchema = z.object({
    username: z
        .string()
        .min(3, "Username must have at least 3 characters"),

    password: z
        .string()
        .min(4, "Password must have at least 4 characters"),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function LoginPage() {
    
    const {
        register,
        handleSubmit,
        formState: { errors },
    } = useForm<LoginFormData>({
        resolver: zodResolver(loginSchema)
    });
    
    const navigate = useNavigate();

    const onSubmit = async (data: LoginFormData) => {
        try {
            const response = await login(data);

            saveTokens(
                response.accessToken, 
                response.refreshToken
            );

        navigate('/dashboard');

        } catch (error) {
            console.error("Login failed:", error);
        }
    };

    return (
        <div>
            <h1>Login</h1>

            <form onSubmit={handleSubmit(onSubmit)}>
                <div>
                <input
                    type="text"
                    placeholder="Username"
                    {...register("username")}
                />
                {errors.username && (
                    <p>{errors.username.message}</p>
                )}
                </div>

                <div>
                <input
                    type="password"
                    placeholder="Password"
                    {...register("password")}
                />
                {errors.password && (
                    <p>{errors.password.message}</p>
                )}
                </div>

                <button type="submit">
                    Login
                </button>

            </form>

            <Link to="/register">
                Create an account
            </Link>
        </div>
    );
}
