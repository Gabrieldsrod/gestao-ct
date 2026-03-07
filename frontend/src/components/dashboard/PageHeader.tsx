interface PageHeaderProps {
    title: string;
    subtitle: string;
}

export function PageHeader({ title, subtitle }: PageHeaderProps) {
    return (
        <div className="mb-8">
            <h2 className="text-3xl font-bold tracking-tight text-gray-900">{title}</h2>
            <p className="text-sm text-muted-foreground mt-1">{subtitle}</p>
        </div>
    )
}