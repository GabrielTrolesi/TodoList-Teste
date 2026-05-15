import { act, fireEvent, render, screen } from "@testing-library/react";
import { TodoCard } from "@/features/todos/TodoCard";
import type { Todo } from "@/lib/types";

const todo: Todo = {
  id: "todo-1",
  title: "Revisar backlog",
  description: "Separar o que vai para a sprint",
  completed: false,
  createdAt: "2026-05-14T12:00:00Z",
  updatedAt: "2026-05-14T12:00:00Z",
  completedAt: null
};

describe("TodoCard", () => {
  it("renders title, description and actions", () => {
    render(<TodoCard todo={todo} onSave={vi.fn()} onComplete={vi.fn()} onDelete={vi.fn()} />);

    expect(screen.getByText("Revisar backlog")).toBeInTheDocument();
    expect(screen.getByText("Separar o que vai para a sprint")).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Editar" })).toBeInTheDocument();
    expect(screen.getByRole("button", { name: "Concluir" })).toBeInTheDocument();
  });

  it("switches to edit mode and saves changes", async () => {
    const onSave = vi.fn();

    render(<TodoCard todo={todo} onSave={onSave} onComplete={vi.fn()} onDelete={vi.fn()} />);

    fireEvent.click(screen.getByRole("button", { name: "Editar" }));
    fireEvent.change(screen.getByLabelText("Titulo"), { target: { value: "Backlog final" } });
    fireEvent.change(screen.getByLabelText("Descricao"), { target: { value: "Sprint ajustada" } });
    await act(async () => {
      fireEvent.submit(screen.getByRole("button", { name: "Salvar" }));
    });

    expect(onSave).toHaveBeenCalledWith("todo-1", {
      title: "Backlog final",
      description: "Sprint ajustada"
    });
  });
});
