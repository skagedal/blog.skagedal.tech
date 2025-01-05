---

---

Let's say you want to write a clock in React. Something that shows the time. First, let's have a component that shows the time: 

```tsx
function WatchFace({ date }: { date: Date }) {
    return <div style={{ textAlign: "center" }}>
        {date.toLocaleTimeString()}
    </div>;
}
```

And then put that it our app:

```tsx
export default function App() {
    const date = new Date();
    return <WatchFace date={date} />;
}
```

We're successfully showing time! Yay! You can try it out [here](https://codesandbox.io/p/sandbox/dead-clock-zp33p4?workspaceId=ws_BCcGvx7jsHvLEV4kMA81L6)! You just simply need to refresh the page to update the clock.  

But ok, ok. We want it to automatically update as time goes on. We need something that will trigger a rerender of our component when some time has passed. 

The standard DOM API to do this is `setInterval`. It's a function that takes a callback and a time interval in milliseconds. It will call the callback every time the interval has passed.  

Using an API like this in a React component is a by-the-book example of where you should use `useEffect`, as this is about "[synchronizing with an external system](https://react.dev/learn/synchronizing-with-effects)" – that is, something that operates outside of the reactive data flow. We should set up our interval in a `useEffect` and also make sure to clean up afterwards. This works:

With our `WatchFace` component as before, we can rewrite our `App` component like this:

```tsx
export default function App() {
    const [date, setDate] = useState(new Date());
    useEffect(() => {
        const interval = setInterval(() => setDate(new Date()), 1000);
        return () => clearInterval(interval);
    }, []);

    return <WatchFace date={date} />;
}
```

This works! Chances are that if you go to my CodeSandbox and try it out, it will work fine!

Note that we're passing an empty dependency array – which is fine by the rules; we're not using anything except `setDate` which is a stable reference. If we excluded the dependency array, it would run the effect every time, clearing and setting a new interval every second. 

But what if I told you that if I had a thousand readers of this blog post, on average, one of these would have a suboptimally working clock when clicking the above link? One that doesn't neatly tick at every second, instead jumping two seconds every now and then? That would be very sad! But it is, in fact, the case! 

Let's add some more stuff to see what I'm talking about. First, let's also show the current milliseconds in our watchface:

```tsx
function WatchFace({ date }: { date: Date }) {
    return (
        <div style={{ textAlign: "center" }}>
            <p>{date.toLocaleTimeString()}</p>
            <p>{date.getMilliseconds()}</p>
        </div>
    );
}
```

You'll find that on each update, the same millisecond value will be about the same. For me, right now, it goes back and forth between 678 and 679. This is of course depend=ent on all kinds of timing and performance details of your machine. In the CodeSandbox, if you reload the preview widget, you will see that it aligns around a different millisecond – it will stay whatever it was when you started the `setInterval`, and then there will be a variable amount of time from the low-level timer interrupt until your JavaScript code actually gets to the `new Date()`.        

But what if the 

- 
- [Code Sandbox Experiment](https://codesandbox.io/p/sandbox/react-dev-forked-4y9shw?file=%2Fsrc%2FuseInterval.js%3A14%2C34)
- [Overreacted: Making setInterval Declarative with React Hooks](https://overreacted.io/making-setinterval-declarative-with-react-hooks/)
- MDN documentation for setInterval: https://developer.mozilla.org/en-US/docs/Web/API/Window/setInterval
- Example of bug: https://www.npmjs.com/package/react-clock
- 