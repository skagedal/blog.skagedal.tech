"use client";

import { useState } from "react";
import { Card, CardContent, CardFooter } from "./ui/card";
import { Textarea } from "./ui/textarea";
import { Loader2, MessageSquare } from "lucide-react";
import styles from "./comments.module.css";

const MAX_CHARS = 1000;

export function CommentForm() {
  const [content, setContent] = useState("");
  const remainingChars = MAX_CHARS - content.length;
  const isOverLimit = remainingChars < 0;
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    await new Promise((resolve) => setTimeout(resolve, 2000)); // Simulate a network request
    console.log("Form submitted");
    setIsSubmitting(false);
  };

  return (
    <div className="page-content">
      <div className="wrapper">
        <Card>
          <form onSubmit={handleSubmit}>
            <CardContent>
              <h2>
                <MessageSquare className={styles.icon} />
                Comments
              </h2>
              <Textarea
                placeholder="Leave a comment..."
                value={content}
                onChange={(e) => setContent(e.target.value)}
              />
              <div
                className={`${styles.remaining} ${
                  isOverLimit ? styles.destructive : styles.muted
                }`}
              >
                {remainingChars} characters remaining
              </div>
            </CardContent>
            <CardFooter>
              <p>
                Comments are under development. Nothing happens when you submit
                them.
              </p>
              <button
                type="submit"
                disabled={isOverLimit || content.trim() === "" || isSubmitting}
              >
                {isSubmitting ? (
                  <>
                    <Loader2 className={styles.iconspinner} />
                    Submitting
                  </>
                ) : (
                  "Submit Comment"
                )}
              </button>
            </CardFooter>
          </form>
        </Card>
      </div>
    </div>
  );
}
