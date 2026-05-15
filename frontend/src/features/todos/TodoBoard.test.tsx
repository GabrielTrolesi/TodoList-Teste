import { render, screen, waitFor } from "@testing-library/react";
import { TodoBoard } from "@/features/todos/TodoBoard";
import { ApiError } from "@/lib/api";

const readSession = vi.fn();
const saveSession = vi.fn();
const clearSession = vi.fn();
const navigateTo = vi.fn();

const fetchCurrentUser = vi.fn();
const fetchTodos = vi.fn();
const createTodo = vi.fn();
const updateTodo = vi.fn();
const completeTodo = vi.fn();
const deleteTodo = vi.fn();

vi.mock("@/lib/session", () => ({
  readSession: () => readSession(),
  saveSession: (...args: unknown[]) => saveSession(...args),
  clearSession: () => clearSession(),
  navigateTo: (...args: unknown[]) => navigateTo(...args)
}));

vi.mock("@/lib/api", async () => {
  const actual = await vi.importActual<typeof import("@/lib/api")>("@/lib/api");
  return {
    ...actual,
    fetchCurrentUser: (...args: unknown[]) => fetchCurrentUser(...args),
    fetchTodos: (...args: unknown[]) => fetchTodos(...args),
    createTodo: (...args: unknown[]) => createTodo(...args),
    updateTodo: (...args: unknown[]) => updateTodo(...args),
    completeTodo: (...args: unknown[]) => completeTodo(...args),
    deleteTodo: (...args: unknown[]) => deleteTodo(...args)
  };
});

describe("TodoBoard", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    readSession.mockReturnValue({
      accessToken: "token-123"
    });
    fetchCurrentUser.mockResolvedValue({
      id: "user-1",
      name: "Ana",
      email: "ana@example.com",
      createdAt: "2026-05-14T10:00:00Z"
    });
    fetchTodos.mockResolvedValue([]);
  });

  it("keeps the session when dashboard loading has a generic error", async () => {
    fetchTodos.mockRejectedValue(new Error("Falha temporaria"));

    render(<TodoBoard />);

    await waitFor(() => {
      expect(screen.getByText("Falha temporaria")).toBeInTheDocument();
    });

    expect(clearSession).not.toHaveBeenCalled();
    expect(navigateTo).not.toHaveBeenCalledWith("/login", "replace");
  });

  it("clears the session and redirects on unauthorized load", async () => {
    fetchCurrentUser.mockRejectedValue(new ApiError("Nao autorizado", 401));

    render(<TodoBoard />);

    await waitFor(() => {
      expect(clearSession).toHaveBeenCalled();
    });

    expect(navigateTo).toHaveBeenCalledWith("/login", "replace");
  });
});
