import { fireEvent, render, screen } from "@testing-library/react";
import { AuthFormCard } from "@/features/auth/AuthFormCard";

describe("AuthFormCard", () => {
  it("renders register fields and submits name, email and password", async () => {
    const onSubmit = vi.fn();

    render(<AuthFormCard mode="register" onSubmit={onSubmit} />);

    fireEvent.change(screen.getByLabelText("Nome"), { target: { value: "Ana" } });
    fireEvent.change(screen.getByLabelText("Email"), { target: { value: "ana@example.com" } });
    fireEvent.change(screen.getByLabelText("Senha"), { target: { value: "password123" } });
    fireEvent.submit(screen.getByRole("button", { name: "Criar conta" }));

    expect(onSubmit).toHaveBeenCalledWith({
      name: "Ana",
      email: "ana@example.com",
      password: "password123"
    });
  });

  it("does not render the name field in login mode", () => {
    render(<AuthFormCard mode="login" onSubmit={vi.fn()} />);

    expect(screen.queryByLabelText("Nome")).not.toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Entrar" })).toBeInTheDocument();
  });
});
