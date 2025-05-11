import Link from "next/link";

export default function Home() {
  return (
    <>
      Go to <Link href="/comments">Comments</Link> page to see the comments
      form.
    </>
  );
}
