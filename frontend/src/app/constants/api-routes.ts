import { environment as env} from 'src/environments/environment';

export const ApiRoutes = {
    AUTH: {
        ACCESS: `${env.urlApi}/access`,
        VERIFY:`${env.urlApi}/verify/`,
        TEACHER_LOGGED: `${env.urlApi}/teacherLogged`,
        LOGOUT: `${env.urlApi}/logout`
    }
}
