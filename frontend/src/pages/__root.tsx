import { createRootRoute, Outlet } from '@tanstack/react-router'
import { TanStackRouterDevtools } from '@tanstack/react-router-devtools'
import { Sidebar } from '../components/Sidebar'
import { Header } from '../components/Header'

export const Route = createRootRoute({
  component: () => (
    <div className="flex h-screen bg-slate-50 font-sans">
      <Sidebar />
      <main className="flex-1 flex flex-col overflow-y-auto">
        <div className="sticky top-0 z-20 bg-slate-50 shadow-sm">
          <Header />
        </div>
        <Outlet /> 
      </main>
      <TanStackRouterDevtools position="bottom-right" />
    </div>
  ),
})