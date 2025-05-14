import { ExampleBlogPost, ExampleFooter } from "@/components/blogdans-examples";
import { CommentForm } from "@/components/comments";
import { SiteHeader } from "@/components/site-header";

export default function CommentsPage() {
  return (
    <div>
      <SiteHeader />
      <ExampleBlogPost />
      <CommentForm />
      <ExampleFooter />
    </div>
  );
}
