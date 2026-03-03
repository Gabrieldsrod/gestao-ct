import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import { RouterProvider, createRouter } from '@tanstack/react-router'
import './index.css'

// Importa a árvore de rotas gerada pelo Vite
import { routeTree } from './route-tree.gen.ts'

// Cria a instância do roteador
const router = createRouter({ routeTree })

// Registra o roteador para ter tipagem global (Type-Safety mágico do TanStack)
declare module '@tanstack/react-router' {
  interface Register {
    router: typeof router
  }
}

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    {/* Sai o <App />, entra o roteador gerenciando tudo */}
    <RouterProvider router={router} />
  </StrictMode>,
)