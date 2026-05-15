import { useState } from "react";
import { ActionButton } from "@/components/ui/ActionButton";
import { TextField } from "@/components/ui/TextField";

type Mode = "login" | "register";

type Props = {
  mode: Mode;
  pending?: boolean;
  errorMessage?: string;
  onSubmit: (payload: { name?: string; email: string; password: string }) => Promise<void> | void;
};

export function AuthFormCard({ mode, pending = false, errorMessage, onSubmit }: Props) {
  const [name, setName] = useState("");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const isRegister = mode === "register";

  async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await onSubmit({
      name: isRegister ? name : undefined,
      email,
      password
    });
  }

  return (
    <section className="auth-card">
      <div className="auth-card__eyebrow">{isRegister ? "Criar acesso" : "Acessar conta"}</div>
      <h1 className="auth-card__title">{isRegister ? "Comece sua rotina com clareza." : "Entre e veja o que precisa acontecer hoje."}</h1>
      <p className="auth-card__text">
        {isRegister
          ? "Crie sua conta para organizar tarefas com uma interface direta e sem ruído."
          : "Use seu email e senha para abrir sua lista pessoal de tarefas."}
      </p>

      <form className="auth-card__form" onSubmit={handleSubmit}>
        {isRegister ? (
          <TextField
            label="Nome"
            name="name"
            value={name}
            autoComplete="name"
            onChange={(event) => setName(event.target.value)}
            placeholder="Seu nome"
            required
          />
        ) : null}
        <TextField
          label="Email"
          name="email"
          type="email"
          value={email}
          autoComplete="email"
          onChange={(event) => setEmail(event.target.value)}
          placeholder="voce@empresa.com"
          required
        />
        <TextField
          label="Senha"
          name="password"
          type="password"
          value={password}
          autoComplete={isRegister ? "new-password" : "current-password"}
          onChange={(event) => setPassword(event.target.value)}
          placeholder="Minimo de 8 caracteres"
          required
        />

        {errorMessage ? <div className="form-message form-message--error">{errorMessage}</div> : null}

        <ActionButton type="submit" fullWidth disabled={pending}>
          {pending ? "Enviando..." : isRegister ? "Criar conta" : "Entrar"}
        </ActionButton>
      </form>

      <div className="auth-card__footer">
        {isRegister ? (
          <a href="/login">Ja tenho conta</a>
        ) : (
          <a href="/register">Criar conta agora</a>
        )}
      </div>
    </section>
  );
}
