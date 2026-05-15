import type { AuthResponse, Todo, User } from "@/lib/types";

const API_BASE_URL = import.meta.env.PUBLIC_API_BASE_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  constructor(
    message: string,
    public readonly status: number
  ) {
    super(message);
    this.name = "ApiError";
  }
}

type HttpMethod = "GET" | "POST" | "PUT" | "PATCH" | "DELETE";

type RequestOptions = {
  method?: HttpMethod;
  body?: unknown;
  token?: string | null;
};

type ApiErrorPayload = {
  message?: string;
};

async function request<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: options.method ?? "GET",
    headers: {
      "Content-Type": "application/json",
      ...(options.token ? { Authorization: `Bearer ${options.token}` } : {})
    },
    body: options.body ? JSON.stringify(options.body) : undefined
  });

  if (!response.ok) {
    const payload = (await response.json().catch(() => null)) as ApiErrorPayload | null;
    throw new ApiError(
      payload?.message ?? "Nao foi possivel concluir a solicitacao.",
      response.status
    );
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

export function saveToken(token: string) {
  localStorage.setItem("todolist.accessToken", token);
}

export function readToken() {
  return localStorage.getItem("todolist.accessToken");
}

export function clearToken() {
  localStorage.removeItem("todolist.accessToken");
}

export function registerUser(payload: { name: string; email: string; password: string }) {
  return request<AuthResponse>("/api/auth/register", {
    method: "POST",
    body: payload
  });
}

export function loginUser(payload: { email: string; password: string }) {
  return request<AuthResponse>("/api/auth/login", {
    method: "POST",
    body: payload
  });
}

export function fetchCurrentUser(token: string) {
  return request<User>("/api/users/me", { token });
}

export function fetchTodos(token: string) {
  return request<Todo[]>("/api/todos", { token });
}

export function createTodo(token: string, payload: { title: string; description?: string }) {
  return request<Todo>("/api/todos", {
    method: "POST",
    token,
    body: payload
  });
}

export function updateTodo(
  token: string,
  todoId: string,
  payload: { title: string; description?: string }
) {
  return request<Todo>(`/api/todos/${todoId}`, {
    method: "PUT",
    token,
    body: payload
  });
}

export function completeTodo(token: string, todoId: string) {
  return request<Todo>(`/api/todos/${todoId}/complete`, {
    method: "PATCH",
    token
  });
}

export function deleteTodo(token: string, todoId: string) {
  return request<void>(`/api/todos/${todoId}`, {
    method: "DELETE",
    token
  });
}
