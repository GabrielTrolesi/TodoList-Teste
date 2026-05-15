type Props = {
  completed: boolean;
};

export function StatusPill({ completed }: Props) {
  return <span className={`status-pill ${completed ? "is-complete" : "is-pending"}`}>{completed ? "Concluida" : "Pendente"}</span>;
}
