import type { MDXComponents } from "mdx/types";

// https://nextjs.org/docs/app/api-reference/file-conventions/mdx-components

export function useMDXComponents(components: MDXComponents): MDXComponents {
  return {
    ...components,
  };
}
