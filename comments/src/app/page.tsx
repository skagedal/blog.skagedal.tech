import { ExampleBlogPost, ExampleFooter } from "@/components/blogdans-examples";
import { CommentForm } from "@/components/comments";

export default function Home() {
  return (
    <>
      <ExampleBlogPost />
      <CommentForm />
      <ExampleFooter />
    </>
  );
}
