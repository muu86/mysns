'use client';

import React, {
  PropsWithChildren,
  createContext,
  useContext,
  useState,
} from 'react';

const AuthContext = createContext({ authenticated: false });

export const AuthProvider: React.FC<PropsWithChildren> = ({ children }) => {
  const [authenticated, setAuthenticated] = useState(false);

  return (
    <AuthContext.Provider value={{ authenticated }}></AuthContext.Provider>
  );
};

export const useAuth = () => useContext(AuthContext);
