import { useState } from "react";
import { ActionButton } from "@/components/ui/ActionButton";
import { StatusPill } from "@/components/ui/StatusPill";
import { TextField } from "@/components/ui/TextField";
import type { Todo } from "@/lib/types";

type Props = {
  todo: Todo;
  onSave: (todoId: string, payload: { title: string; description?: string }) => Promise<void> | void;
  onComplete: (todoId: string) => Promise<void> | void;
  onDelete: (todoId: string) => Promise<void> | void;
};

export function TodoCard({ todo, onSave, onComplete, onDelete }: Props) {
  const [editing, setEditing] = useState(false);
  const [title, setTitle] = useState(todo.title);
  const [description, setDescription] = useState(todo.description ?? "");

  async function handleSave(event: React.FormEvent<HTMLFormElement>) {
    event.preventDefault();
    await onSave(todo.id, {
      title,
      description
    });
    setEditing(false);
  }

  return (
    <article className={`todo-card ${todo.completed ? "todo-card--complete" : ""}`}>
      <div className="todo-card__meta">
        <StatusPill completed={todo.completed} />
        <span className="todo-card__date">
          {new Date(todo.updatedAt).toLocaleDateString("pt-BR", {
            day: "2-digit",
            month: "short"
          })}
        </span>
      </div>

      {editing ? (
        <form className="todo-card__edit" onSubmit={handleSave}>
          <TextField
            label="Titulo"
            value={title}
            onChange={(event) => setTitle(event.target.value)}
            maxLength={120}
            required
          />
          <TextField
            label="Descricao"
            value={description}
            onChange={(event) => setDescription(event.target.value)}
            multiline
            rows={4}
            maxLength={1000}
          />
          <div className="todo-card__actions">
            <ActionButton type="submit">Salvar</ActionButton>
            <ActionButton type="button" variant="ghost" onClick={() => setEditing(false)}>
              Cancelar
            </ActionButton>
          </div>
        </form>
      ) : (
        <>
          <div className="todo-card__body">
            <h2>{todo.title}</h2>
            <p>{todo.description || "Sem descricao adicional."}</p>
          </div>

          <div className="todo-card__actions">
            <ActionButton type="button" variant="secondary" onClick={() => setEditing(true)}>
              Editar
            </ActionButton>
            <ActionButton type="button" variant="ghost" onClick={() => onComplete(todo.id)} disabled={todo.completed}>
              {todo.completed ? "Concluida" : "Concluir"}
            </ActionButton>
            <ActionButton type="button" variant="danger" onClick={() => onDelete(todo.id)}>
              Excluir
            </ActionButton>
          </div>
        </>
      )}
    </article>
  );
}
