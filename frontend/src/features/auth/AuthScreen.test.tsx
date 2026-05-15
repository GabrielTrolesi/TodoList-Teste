import { act, fireEvent, render, screen, waitFor } from "@testing-library/react";
import { AuthScreen } from "@/features/auth/AuthScreen";
import type { AuthResponse } from "@/lib/types";

const navigateTo = vi.fn();
const saveSession = vi.fn();
const readSession = vi.fn();
const fetchCurrentUser = vi.fn();
const loginUser = vi.fn();
const registerUser = vi.fn();

vi.mock("@/lib/session", () => ({
  navigateTo: (...args: unknown[]) => navigateTo(...args),
  saveSession: (...args: unknown[]) => saveSession(...args),
  readSession: () => readSession()
}));

vi.mock("@/lib/api", () => ({
  fetchCurrentUser: (...args: unknown[]) => fetchCurrentUser(...args),
  loginUser: (...args: unknown[]) => loginUser(...args),
  registerUser: (...args: unknown[]) => registerUser(...args)
}));

describe("AuthScreen", () => {
  beforeEach(() => {
    vi.clearAllMocks();
    readSession.mockReturnValue(null);
  });

  it("navigates to todos after successful login", async () => {
    const response: AuthResponse = {
      tokenType: "Bearer",
      accessToken: "token-123",
      user: {
        id: "user-1",
        name: "Ana",
        email: "ana@example.com",
        createdAt: "2026-05-14T10:00:00Z"
      }
    };
    loginUser.mockResolvedValue(response);

    render(<AuthScreen mode="login" />);

    fireEvent.change(screen.getByLabelText("Email"), { target: { value: "ana@example.com" } });
    fireEvent.change(screen.getByLabelText("Senha"), { target: { value: "password123" } });

    await act(async () => {
      fireEvent.submit(screen.getByRole("button", { name: "Entrar" }));
    });

    expect(saveSession).toHaveBeenCalledWith({
      accessToken: "token-123",
      user: response.user
    });
    expect(navigateTo).toHaveBeenCalledWith("/todos", "replace");
  });

  it("redirects to todos when a valid stored session exists", async () => {
    readSession.mockReturnValue({
      accessToken: "token-abc"
    });
    fetchCurrentUser.mockResolvedValue({
      id: "user-1",
      name: "Ana",
      email: "ana@example.com",
      createdAt: "2026-05-14T10:00:00Z"
    });

    render(<AuthScreen mode="login" />);

    await waitFor(() => {
      expect(fetchCurrentUser).toHaveBeenCalledWith("token-abc");
    });
    expect(navigateTo).toHaveBeenCalledWith("/todos", "replace");
  });
});
