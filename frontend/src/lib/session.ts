import type { User } from "@/lib/types";

const TOKEN_STORAGE_KEY = "todolist.accessToken";
const USER_STORAGE_KEY = "todolist.user";

export type StoredSession = {
  accessToken: string;
  user?: User;
};

export function saveSession(session: StoredSession) {
  localStorage.setItem(TOKEN_STORAGE_KEY, session.accessToken);

  if (session.user) {
    localStorage.setItem(USER_STORAGE_KEY, JSON.stringify(session.user));
  }
}

export function readSession(): StoredSession | null {
  const accessToken = localStorage.getItem(TOKEN_STORAGE_KEY);
  if (!accessToken) {
    return null;
  }

  const rawUser = localStorage.getItem(USER_STORAGE_KEY);
  if (!rawUser) {
    return { accessToken };
  }

  try {
    return {
      accessToken,
      user: JSON.parse(rawUser) as User
    };
  } catch {
    return { accessToken };
  }
}

export function clearSession() {
  localStorage.removeItem(TOKEN_STORAGE_KEY);
  localStorage.removeItem(USER_STORAGE_KEY);
}

export function navigateTo(path: string, mode: "assign" | "replace" = "assign") {
  const url = new URL(path, window.location.origin).toString();
  if (mode === "replace") {
    window.location.replace(url);
    return;
  }

  window.location.assign(url);
}
