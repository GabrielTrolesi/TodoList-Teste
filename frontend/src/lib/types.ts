export type User = {
  id: string;
  name: string;
  email: string;
  createdAt: string;
};

export type AuthResponse = {
  tokenType: string;
  accessToken: string;
  user: User;
};

export type Todo = {
  id: string;
  title: string;
  description: string | null;
  completed: boolean;
  createdAt: string;
  updatedAt: string;
  completedAt: string | null;
};
