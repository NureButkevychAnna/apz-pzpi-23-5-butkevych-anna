import { useState } from "react";
import { useAuth } from "../context/AuthContext";

export function useAuthViewModel() {
  const { login: ctxLogin, register: ctxRegister } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [name, setName] = useState("");
  const [isRegister, setIsRegister] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState("");

  const handleSubmit = async (e: React.FormEvent, onSuccess: () => void) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);
    try {
      if (isRegister) {
        await ctxRegister(email, password, name);
      } else {
        await ctxLogin(email, password);
      }
      onSuccess();
    } catch (err) {
      setError(err instanceof Error ? err.message : "Authentication failed");
    } finally {
      setIsLoading(false);
    }
  };

  const toggleMode = () => {
    setIsRegister((prev) => !prev);
    setError("");
  };

  return {
    email,
    setEmail,
    password,
    setPassword,
    name,
    setName,
    isRegister,
    toggleMode,
    isLoading,
    error,
    handleSubmit,
  };
}
