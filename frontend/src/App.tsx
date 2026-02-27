export function App() {
  return (
    // 'flex' coloca os itens lado a lado. 'h-screen' faz ocupar 100% da altura da tela. 'bg-slate-50' é o fundo cinza clarinho.
    <div className="flex h-screen bg-slate-50 font-sans">
      
      {/* 1. BARRA LATERAL (SIDEBAR) */}
      {/* 'w-64' fixa a largura. 'bg-white' fundo branco. 'border-r' cria a linha divisória na direita. */}
      <aside className="w-64 bg-white border-r border-gray-200 flex flex-col">
        <div className="p-6">
          <h1 className="text-xl font-bold text-blue-600">Gestão CT</h1>
        </div>
        <nav className="flex-1 px-4 space-y-2">
          {/* Um item do menu como exemplo */}
          <a href="#" className="block px-4 py-2 rounded-lg bg-blue-50 text-blue-700 font-medium">
            Visão Geral
          </a>
          <a href="#" className="block px-4 py-2 rounded-lg text-gray-600 hover:bg-gray-100 font-medium">
            Alunos
          </a>
        </nav>
      </aside>

      {/* 2. ÁREA PRINCIPAL */}
      {/* 'flex-1' faz essa div ocupar todo o resto do espaço que sobrou. 'overflow-y-auto' permite rolar a página se tiver muito conteúdo. */}
      <main className="flex-1 flex flex-col overflow-y-auto">
        
        {/* CABEÇALHO */}
        <header className="px-8 py-6 flex justify-between items-center bg-white border-b border-gray-200">
          <div>
            <h2 className="text-2xl font-semibold text-gray-800">Gestão de Alunos</h2>
            <p className="text-sm text-gray-500">Acompanhe as métricas do Centro de Treinamento</p>
          </div>
          <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors">
            + Novo Aluno
          </button>
        </header>

        {/* CONTEÚDO DA PÁGINA */}
        <div className="p-8 space-y-6">
          
          {/* CARDS DE RESUMO (Grid com 3 colunas) */}
          <div className="grid grid-cols-3 gap-6">
            <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm border-l-4 border-l-blue-500">
              <h3 className="text-sm font-medium text-gray-500">Total Alunos</h3>
              <p className="text-3xl font-bold text-gray-800 mt-2">125</p>
            </div>
            <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm border-l-4 border-l-green-500">
              <h3 className="text-sm font-medium text-gray-500">Inscrições Ativas</h3>
              <p className="text-3xl font-bold text-gray-800 mt-2">110</p>
            </div>
            <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm border-l-4 border-l-orange-500">
              <h3 className="text-sm font-medium text-gray-500">Pagamentos Pendentes</h3>
              <p className="text-3xl font-bold text-gray-800 mt-2">R$ 1.450,00</p>
            </div>
          </div>

          {/* ÁREA DA TABELA E GRÁFICO (A tabela ocupa 2 espaços, o gráfico 1) */}
          <div className="grid grid-cols-3 gap-6">
            <div className="col-span-2 bg-white rounded-xl border border-gray-200 shadow-sm min-h-[400px] p-6">
               <h3 className="font-semibold text-gray-800 mb-4">Lista de Alunos (Em breve)</h3>
               {/* A tabela vai entrar aqui */}
            </div>
            <div className="col-span-1 bg-white rounded-xl border border-gray-200 shadow-sm min-h-[400px] p-6">
               <h3 className="font-semibold text-gray-800 mb-4">Receita (Em breve)</h3>
               {/* O gráfico vai entrar aqui */}
            </div>
          </div>

        </div>
      </main>

    </div>
  )
}

export default App