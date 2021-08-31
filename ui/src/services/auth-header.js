import AuthService from "../services/auth.service";

export default function authHeader() {
    const user = AuthService.getCurrentUser();

    if (user && user.accessToken) {
        return user.accessToken;
    } else {
        return {};
    }
}
