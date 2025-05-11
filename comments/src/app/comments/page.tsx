import { ExampleBlogPost, ExampleFooter } from "@/components/blogdans-examples";
import { CommentForm } from "@/components/comments";

export default function CommentsPage() {
  return (
    <div>
      <ExampleBlogPost />
      <CommentForm />
      <ExampleFooter />
    </div>
  );
}
