import type { ButtonHTMLAttributes, PropsWithChildren } from "react";

type Props = PropsWithChildren<
  ButtonHTMLAttributes<HTMLButtonElement> & {
    variant?: "primary" | "secondary" | "ghost" | "danger";
    fullWidth?: boolean;
  }
>;

export function ActionButton({
  children,
  variant = "primary",
  fullWidth = false,
  className = "",
  ...props
}: Props) {
  return (
    <button
      className={`button button--${variant}${fullWidth ? " button--full" : ""} ${className}`.trim()}
      {...props}
    >
      {children}
    </button>
  );
}
