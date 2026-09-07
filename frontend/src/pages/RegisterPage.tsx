import {Link} from "react-router-dom";

export default function RegisterPage() {
    return (
        <div>
            <h1>Register Page</h1>
            <Link to="/login">
                Already have an account? Login
            </Link>
        </div>
    );
}