import { useEffect, useState } from "react";
import { ActionButton } from "@/components/ui/ActionButton";
import { TextField } from "@/components/ui/TextField";
import {
  ApiError,
  completeTodo,
  createTodo,
  deleteTodo,
  fetchCurrentUser,
  fetchTodos,
  updateTodo
} from "@/lib/api";
import { clearSession, navigateTo, readSession, saveSession } from "@/lib/session";
import type { Todo, User } from "@/lib/types";
import { TodoCard } from "@/features/todos/TodoCard";

export function TodoBoard() {
  const [user, setUser] = useState<User | null>(null);
  const [todos, setTodos] = useState<Todo[]>([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const [accessToken, setAccessToken] = useState<string | null>(null);

  useEffect(() => {
    const session = readSession();
    if (!session?.accessToken) {
      navigateTo("/login", "replace");
      return;
    }
    setAccessToken(session.accessToken);
    if (session.user) {
      setUser(session.user);
    }

    async function loadDashboard() {
      try {
        setLoading(true);
        const [currentUser, todoItems] = await Promise.all([
          fetchCurrentUser(session.accessToken),
          fetchTodos(session.accessToken)
        ]);
        setUser(currentUser);
        saveSession({
          accessToken: session.accessToken,
          user: currentUser
        });
        setTodos(todoItems);
      } catch (error) {
        if (error instanceof ApiError && (error.status === 401 || error.status === 403)) {
          clearSession();
          navigateTo("/login", "replace");
          return;
        }
        setErrorMessage(error instanceof Error ? error.message : "Nao foi possivel carregar a agenda.");
      } finally {
        setLoading(false);
      }
    }

    void loadDashboard();
  }, []);

  async function handleCreate(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const token = accessToken;
    if (!token) {
      setErrorMessage("Sua sessao nao esta disponivel no momento. Recarregue a pagina.");
      return;
    }

    try {
      setSubmitting(true);
      setErrorMessage("");
      const todo = await createTodo(token, { title, description });
      setTodos((current) => [todo, ...current]);
      setTitle("");
      setDescription("");
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Nao foi possivel criar a tarefa.");
    } finally {
      setSubmitting(false);
    }
  }

  async function handleSave(todoId: string, payload: { title: string; description?: string }) {
    const token = accessToken;
    if (!token) {
      setErrorMessage("Sua sessao nao esta disponivel no momento. Recarregue a pagina.");
      return;
    }
    try {
      setErrorMessage("");
      const updated = await updateTodo(token, todoId, payload);
      setTodos((current) => current.map((todo) => (todo.id === todoId ? updated : todo)));
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Nao foi possivel atualizar a tarefa.");
    }
  }

  async function handleComplete(todoId: string) {
    const token = accessToken;
    if (!token) {
      setErrorMessage("Sua sessao nao esta disponivel no momento. Recarregue a pagina.");
      return;
    }
    try {
      setErrorMessage("");
      const updated = await completeTodo(token, todoId);
      setTodos((current) => current.map((todo) => (todo.id === todoId ? updated : todo)));
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Nao foi possivel concluir a tarefa.");
    }
  }

  async function handleDelete(todoId: string) {
    const token = accessToken;
    if (!token) {
      setErrorMessage("Sua sessao nao esta disponivel no momento. Recarregue a pagina.");
      return;
    }
    try {
      setErrorMessage("");
      await deleteTodo(token, todoId);
      setTodos((current) => current.filter((todo) => todo.id !== todoId));
    } catch (error) {
      setErrorMessage(error instanceof Error ? error.message : "Nao foi possivel excluir a tarefa.");
    }
  }

  function handleLogout() {
    clearSession();
    navigateTo("/login", "replace");
  }

  const pendingTodos = todos.filter((todo) => !todo.completed);
  const completedTodos = todos.filter((todo) => todo.completed);

  if (loading) {
    return <section className="dashboard-shell"><div className="panel">Carregando sua rotina...</div></section>;
  }

  return (
    <section className="dashboard-shell">
      <header className="dashboard-hero">
        <div>
          <div className="dashboard-hero__eyebrow">Painel pessoal</div>
          <h1>{user ? `${user.name}, aqui esta o foco do dia.` : "Sua agenda de tarefas."}</h1>
          <p>Crie, acompanhe e conclua suas tarefas em uma visualizacao simples, organizada e direta.</p>
        </div>
        <ActionButton type="button" variant="ghost" onClick={handleLogout}>
          Sair
        </ActionButton>
      </header>

      <div className="dashboard-grid">
        <section className="panel panel--composer">
          <div className="panel__header">
            <h2>Nova tarefa</h2>
            <span>{todos.length} itens no total</span>
          </div>

          <form className="composer-form" onSubmit={handleCreate}>
            <TextField
              label="Titulo"
              value={title}
              onChange={(event) => setTitle(event.target.value)}
              placeholder="Ex: revisar entregas"
              maxLength={120}
              required
            />
            <TextField
              label="Descricao"
              value={description}
              onChange={(event) => setDescription(event.target.value)}
              multiline
              rows={5}
              placeholder="Detalhes opcionais para dar contexto a tarefa"
              maxLength={1000}
            />
            {errorMessage ? <div className="form-message form-message--error">{errorMessage}</div> : null}
            <ActionButton type="submit" disabled={submitting}>
              {submitting ? "Salvando..." : "Adicionar tarefa"}
            </ActionButton>
          </form>
        </section>

        <section className="todo-columns">
          <div className="todo-column">
            <div className="todo-column__header">
              <h2>Em andamento</h2>
              <span>{pendingTodos.length}</span>
            </div>
            <div className="todo-column__list">
              {pendingTodos.length ? (
                pendingTodos.map((todo) => (
                  <TodoCard
                    key={todo.id}
                    todo={todo}
                    onSave={handleSave}
                    onComplete={handleComplete}
                    onDelete={handleDelete}
                  />
                ))
              ) : (
                <div className="empty-card">Nenhuma tarefa pendente. Hora boa para puxar a proxima prioridade.</div>
              )}
            </div>
          </div>

          <div className="todo-column">
            <div className="todo-column__header">
              <h2>Concluidas</h2>
              <span>{completedTodos.length}</span>
            </div>
            <div className="todo-column__list">
              {completedTodos.length ? (
                completedTodos.map((todo) => (
                  <TodoCard
                    key={todo.id}
                    todo={todo}
                    onSave={handleSave}
                    onComplete={handleComplete}
                    onDelete={handleDelete}
                  />
                ))
              ) : (
                <div className="empty-card">Quando as tarefas forem concluídas, elas aparecem aqui.</div>
              )}
            </div>
          </div>
        </section>
      </div>
    </section>
  );
}
