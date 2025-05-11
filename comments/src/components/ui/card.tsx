import React from "react";
import styles from "./card.module.css";
import classNames from "classnames/bind";

const cx = classNames.bind(styles);

const Card = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement>
>(({ className, ...props }, ref) => (
  <div ref={ref} className={cx(styles.card, className)} {...props} />
));
Card.displayName = "Card";

const CardContent = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement>
>(({ className, ...props }, ref) => (
  <div ref={ref} className={cx(styles.cardcontent, className)} {...props} />
));
CardContent.displayName = "CardContent";

const CardFooter = React.forwardRef<
  HTMLDivElement,
  React.HTMLAttributes<HTMLDivElement>
>(({ className, ...props }, ref) => (
  <div ref={ref} className={cx(styles.cardfooter, className)} {...props} />
));
CardFooter.displayName = "CardFooter";

export { Card, CardContent, CardFooter };
