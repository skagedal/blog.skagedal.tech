import fs from "fs";
import path from "path";

function getMDXFiles(dir: string) {
  return fs.readdirSync(dir).filter((file) => path.extname(file) === ".md");
}
export function getBlogPosts() {
  return getMDXFiles(
    path.join(process.cwd(), "src", "app", "content", "posts")
  );
}

export default async function Page({
  params,
}: {
  params: Promise<{ slug: string }>;
}) {
  const slug = (await params).slug;
  console.log(slug);
  const { default: Post, ...other } = await import(`@/app/content/test.mdx`);
  console.log(other);

  return <Post />;
}

export function BlogPosts() {
  const posts = getBlogPosts();
  return (
    <>
      <Page params={Promise.resolve({ slug: "2018-01-02-simcd" })} />
      <ol>
        {posts.map((post) => (
          <li key={post}>{post}</li>
        ))}
      </ol>
    </>
  );
}
