import { useEffect, useState } from "react";
import { fetchCurrentUser, loginUser, registerUser } from "@/lib/api";
import { AuthFormCard } from "@/features/auth/AuthFormCard";
import { navigateTo, readSession, saveSession } from "@/lib/session";

type Props = {
  mode: "login" | "register";
};

export function AuthScreen({ mode }: Props) {
  const [pending, setPending] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  useEffect(() => {
    const session = readSession();
    if (!session?.accessToken) {
      return;
    }

    async function validateSession() {
      try {
        const user = await fetchCurrentUser(session.accessToken);
        saveSession({
          accessToken: session.accessToken,
          user
        });
        navigateTo("/todos", "replace");
      } catch {
        // Let the user continue on the auth page when the stored token is stale.
      }
    }

    void validateSession();
  }, []);

  async function handleSubmit(payload: { name?: string; email: string; password: string }) {
    try {
      setPending(true);
      setErrorMessage("");

      const response =
        mode === "register"
          ? await registerUser({
              name: payload.name ?? "",
              email: payload.email,
              password: payload.password
            })
          : await loginUser({
              email: payload.email,
              password: payload.password
            });

      saveSession({
        accessToken: response.accessToken,
        user: response.user
      });
      navigateTo("/todos", "replace");
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Nao foi possivel autenticar.");
    } finally {
      setPending(false);
    }
  }

  return <AuthFormCard mode={mode} pending={pending} errorMessage={errorMessage} onSubmit={handleSubmit} />;
}
