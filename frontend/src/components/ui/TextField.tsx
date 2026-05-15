import type { InputHTMLAttributes, TextareaHTMLAttributes } from "react";

type BaseProps = {
  label: string;
  hint?: string;
};

type InputProps = BaseProps &
  InputHTMLAttributes<HTMLInputElement> & {
    multiline?: false;
  };

type TextareaProps = BaseProps &
  TextareaHTMLAttributes<HTMLTextAreaElement> & {
    multiline: true;
  };

type Props = InputProps | TextareaProps;

export function TextField(props: Props) {
  const { label, hint, multiline = false, ...fieldProps } = props;

  return (
    <label className="field">
      <span className="field__label">{label}</span>
      {multiline ? (
        <textarea className="field__control field__control--textarea" {...fieldProps} />
      ) : (
        <input className="field__control" {...fieldProps} />
      )}
      {hint ? <span className="field__hint">{hint}</span> : null}
    </label>
  );
}
