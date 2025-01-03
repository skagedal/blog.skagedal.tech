import Image from "next/image";
import Link from "next/link";
import { BlogPosts } from "./components/posts";

export default function Home() {
  return (
    <>
      <header className="border-b-[1px] border-black dark:border-white min-h-[56px]">
        <div className="max-w-screen-md mx-auto px-8 font-[family-name:var(--font-geist-sans)]">
          <Link className="float-left text-2xl leading-[56px] mb-0" href="/">
            skagedal.tech
          </Link>
          <nav className="float-right leading-[56px]">
            <Image
              className="dark:invert block sm:hidden"
              src="/menu.svg"
              alt="Menu"
              width="18"
              height="15"
            />
            <div className="hidden sm:block">
              <Link href="/about">About</Link>
            </div>
          </nav>
        </div>
      </header>
      <div className="max-w-screen-md mx-auto px-8 font-[family-name:var(--font-geist-sans)]">
        <main className="">
          <h1 className="mt-8 mb-4 text-xl font-bold">Nice to meet you!</h1>
          <p className="my-2">
            My name is Simon Kågedal Reimer and I live in Uppsala, Sweden.{" "}
          </p>
          <BlogPosts />
        </main>
      </div>
    </>
  );
}
