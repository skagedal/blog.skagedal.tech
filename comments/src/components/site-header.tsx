import { LucideMenu } from "lucide-react";

export interface PageLinkProps {
  title: string;
  href: string;
}

function PageLink({ title, href }: PageLinkProps) {
  return (
    <div className="trigger">
      <a href={href}>{title}</a>
    </div>
  );
}

export function SiteHeader({
  includeAbout = false,
}: {
  includeAbout?: boolean;
}) {
  return (
    <header className="site-header">
      <div className="wrapper">
        <a href="https://blog.skagedal.tech/" className="site-title">
          skagedal.tech
        </a>
        <nav className="site-nav">
          <a href="#" className="menu-icon">
            <LucideMenu />
          </a>
          {includeAbout && <PageLink title="About" href="/about/" />}
        </nav>
      </div>
    </header>
  );
}
