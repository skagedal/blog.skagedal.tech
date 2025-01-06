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

But what if I told you that if I had a thousand readers of this blog post, on average, one of these would have a suboptimally working clock when clicking the above link? One that doesn't neatly tick at every second, instead jumping two seconds every now and then? This is, in fact, the case. 

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

You'll find that on each update, the  millisecond value will be about the same. For me, right now, it goes back and forth between 678 and 679. This is of course depend=ent on all kinds of timing and performance details of your machine. In the CodeSandbox, if you reload the preview widget, you will see that it aligns around a different millisecond – it will stay whatever it was when you started the `setInterval`, and then there will be a variable amount of time from the low-level timer interrupt until your JavaScript code actually gets to the `new Date()`.        

But what if it starts at the 999:th millisecond? Then every other second it will fall over to the second after! So, we may go from 06:45:03:999 to 06:45:04:999 to 06:45:06:000 – thus never displaying 06:45:05.

You could replicate this by reloading the CodeSandbox, but you'll have to do it on average a thousand times to trigger the bug. Not fun. For our testing purposes, we could force it to trigger by adding an appropriate delay before the interval starts. Somethine like this works:

```tsx
useEffect(() => {
    const wait = 998 - new Date().getMilliseconds();
    let interval: number | undefined = undefined;
    const timeout = setTimeout(() => {
        interval = setInterval(() => setDate(new Date()), 1000);
    }, wait);
    return () => {
        clearTimeout(timeout);
        clearInterval(interval);
    };
}, []);
```

You may have to adjust that number 998 until you get the desired effect. Now you're in the 0.1%! 

**So, how can we fix this?** The easiest way I can think of is to just make more frequent updates. This is probably the best way. Just decide on how big of a lag can be accepted and set your timer interval at that. No one will ever report a problem (I'm not even sure that this would ever happen even without any fix to this problem at all). 

But isn't it just a little bit sad that we have to do this? Come on, a little bit? Can we at least _explore_ other solutions? Yes, of course, my dear.  

One possible solution would be to do what I did just above to trigger the bug – but instead of aligning to the 999:th millisecond, we align to, say, the 100th. 

But this assumes that we can really trust that the interval, once started, will be robustly stay on the same millisecond alignment. This does not seem to be the case, for example, on my machine, when it goes to sleep, the timer interval may have a different phase when it comes back. So then we may still get that same bug.    

A much better solution seems to be just use `setTimeout` instead, and schedule a new timeout at the appropriate point in time. 

Here's how my App now looks:

```tsx
export default function App() {
  const [date, setDate] = useState(new Date());

  useEffect(() => {
    const current = new Date().getMilliseconds();
    const wait = 1000 - current;

    const timeout = setTimeout(() => {
      setDate(new Date());
    }, wait);

    return () => clearTimeout(timeout);
  });

  return (
    <div>
      <WatchFace date={date} />
    </div>
  );
}
```


- 
- [Code Sandbox Experiment](https://codesandbox.io/p/sandbox/react-dev-forked-4y9shw?file=%2Fsrc%2FuseInterval.js%3A14%2C34)
- [Overreacted: Making setInterval Declarative with React Hooks](https://overreacted.io/making-setinterval-declarative-with-react-hooks/)
- MDN documentation for setInterval: https://developer.mozilla.org/en-US/docs/Web/API/Window/setInterval
- Example of bug: https://www.npmjs.com/package/react-clock
- 