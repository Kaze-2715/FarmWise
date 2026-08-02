import { ref } from 'vue';
import { getCurrentUser } from '../api/user';

const currentUser = ref(null);

let currentUserPromise = null;

export const setCurrentUser = (user) => {
    currentUser.value = user;
};

export const clearCurrentUser = () => {
    currentUser.value = null;
};

export const loadCurrentUser = async () => {
    if (currentUser.value) {
        return currentUser.value;
    }

    if (currentUserPromise) {
        return currentUserPromise;
    }

    currentUserPromise = getCurrentUser()
        .then(user => {
            setCurrentUser(user);
            return user;
        })
        .catch(error => {
            clearCurrentUser();
            throw error;
        })
        .finally(() => {
            currentUserPromise = null;
        });

    return currentUserPromise;
};

export const useAuthSession = () => ({
    currentUser,
    loadCurrentUser,
    setCurrentUser,
    clearCurrentUser
});
