So I wanted to create a little Next.js-app. Using the bleeding edge version, here's what I ran: 

```shell
npx create-next-app@canary
```

I answered the various questions:

```
❯ npx create-next-app@canary
✔ What is your project named? … my-app
✔ Would you like to use TypeScript? … No / [Yes]
✔ Would you like to use ESLint? … No / [Yes]
✔ Would you like to use Tailwind CSS? … No / [Yes]
✔ Would you like your code inside a `src/` directory? … No / [Yes]
✔ Would you like to use App Router? (recommended) … No / [Yes]
✔ Would you like to use Turbopack for next dev? … No / [Yes]
✔ Would you like to customize the import alias (@/* by default)? … [No] / Yes
```

And got the output:

```
Creating a new Next.js app in /Users/simon/tmp/eslint-issue/my-app.

Using npm.

Initializing project with template: app-tw 


Installing dependencies:
- react
- react-dom
- next

Installing devDependencies:
- typescript
- @types/node
- @types/react
- @types/react-dom
- postcss
- tailwindcss
- eslint
- eslint-config-next

npm warn deprecated inflight@1.0.6: This module is not supported, and leaks memory. Do not use it. Check out lru-cache if you want a good and tested way to coalesce async requests by a key value, which is much more comprehensive and powerful.
npm warn deprecated rimraf@3.0.2: Rimraf versions prior to v4 are no longer supported
npm warn deprecated glob@7.2.3: Glob versions prior to v9 are no longer supported

added 365 packages, and audited 366 packages in 17s

136 packages are looking for funding
  run `npm fund` for details

found 0 vulnerabilities
Initialized a git repository.

Success! Created my-app at /Users/simon/tmp/eslint-issue/my-app
```

I could have just continued with my life there, but this annoyed me: 

```
npm warn deprecated inflight@1.0.6: This module is not supported, and leaks memory. Do not use it. Check out lru-cache if you want a good and tested way to coalesce async requests by a key value, which is much more comprehensive and powerful.
npm warn deprecated rimraf@3.0.2: Rimraf versions prior to v4 are no longer supported
npm warn deprecated glob@7.2.3: Glob versions prior to v9 are no longer supported
```

These problems seems to be related to the [eslint-config-next](https://www.npmjs.com/package/eslint-config-next) package. A minimal reproducer would be to go in to an empty directory and install this packages as a dev dependency:

```
❯ npm i -D eslint-config-next
npm warn deprecated inflight@1.0.6: This module is not supported, and leaks memory. Do not use it. Check out lru-cache if you want a good and tested way to coalesce async requests by a key value, which is much more comprehensive and powerful.
npm warn deprecated rimraf@3.0.2: Rimraf versions prior to v4 are no longer supported
npm warn deprecated glob@7.2.3: Glob versions prior to v9 are no longer supported

added 291 packages in 2s

122 packages are looking for funding
  run `npm fund` for details
```

Just adding `eslint` as a dev dependency does not trigger the warnings.

```
❯ npm i -D eslint            

added 89 packages in 2s

22 packages are looking for funding
  run `npm fund` for details
```

So the simplest reproducer we have right now is the one were we do just `npm i -D eslint-config-next`.

There is a [Github issue](https://github.com/vercel/next.js/issues/66239) for this. 

We can view the dependency tree that leads to the `inflight` module with `npm why`:

```
❯ npm why inflight                  
inflight@1.0.6 dev peer
node_modules/inflight
  inflight@"^1.0.4" from glob@7.2.3
  node_modules/rimraf/node_modules/glob
    glob@"^7.1.3" from rimraf@3.0.2
    node_modules/rimraf
      rimraf@"^3.0.2" from flat-cache@3.2.0
      node_modules/flat-cache
        flat-cache@"^3.0.4" from file-entry-cache@6.0.1
        node_modules/file-entry-cache
          file-entry-cache@"^6.0.1" from eslint@8.57.0
          node_modules/eslint
            peer eslint@"^6.0.0 || ^7.0.0 || >=8.0.0" from @eslint-community/eslint-utils@4.4.0
            node_modules/@eslint-community/eslint-utils
              @eslint-community/eslint-utils@"^4.2.0" from eslint@8.57.0
            peer eslint@"^8.56.0" from @typescript-eslint/parser@7.2.0
            node_modules/@typescript-eslint/parser
              @typescript-eslint/parser@"^5.4.2 || ^6.0.0 || 7.0.0 - 7.2.0" from eslint-config-next@14.2.3
              node_modules/eslint-config-next
                dev eslint-config-next@"^14.2.3" from the root project
            peer eslint@"^7.23.0 || ^8.0.0" from eslint-config-next@14.2.3
            node_modules/eslint-config-next
              dev eslint-config-next@"^14.2.3" from the root project
            peer eslint@"*" from eslint-import-resolver-typescript@3.6.1
            node_modules/eslint-import-resolver-typescript
              eslint-import-resolver-typescript@"^3.5.2" from eslint-config-next@14.2.3
              node_modules/eslint-config-next
                dev eslint-config-next@"^14.2.3" from the root project
            peer eslint@"^2 || ^3 || ^4 || ^5 || ^6 || ^7.2.0 || ^8" from eslint-plugin-import@2.29.1
            node_modules/eslint-plugin-import
              eslint-plugin-import@"^2.28.1" from eslint-config-next@14.2.3
              node_modules/eslint-config-next
                dev eslint-config-next@"^14.2.3" from the root project
              peer eslint-plugin-import@"*" from eslint-import-resolver-typescript@3.6.1
              node_modules/eslint-import-resolver-typescript
                eslint-import-resolver-typescript@"^3.5.2" from eslint-config-next@14.2.3
                node_modules/eslint-config-next
                  dev eslint-config-next@"^14.2.3" from the root project
            peer eslint@"^3 || ^4 || ^5 || ^6 || ^7 || ^8" from eslint-plugin-jsx-a11y@6.8.0
            node_modules/eslint-plugin-jsx-a11y
              eslint-plugin-jsx-a11y@"^6.7.1" from eslint-config-next@14.2.3
              node_modules/eslint-config-next
                dev eslint-config-next@"^14.2.3" from the root project
            peer eslint@"^3 || ^4 || ^5 || ^6 || ^7 || ^8" from eslint-plugin-react@7.34.2
            node_modules/eslint-plugin-react
              eslint-plugin-react@"^7.33.2" from eslint-config-next@14.2.3
              node_modules/eslint-config-next
                dev eslint-config-next@"^14.2.3" from the root project
            peer eslint@"^3.0.0 || ^4.0.0 || ^5.0.0 || ^6.0.0 || ^7.0.0 || ^8.0.0-0" from eslint-plugin-react-hooks@4.6.2
            node_modules/eslint-plugin-react-hooks
              eslint-plugin-react-hooks@"^4.5.0 || 5.0.0-canary-7118f5dd7-20230705" from eslint-config-next@14.2.3
              node_modules/eslint-config-next
                dev eslint-config-next@"^14.2.3" from the root project
```

The short story is that `inflight` gets included through `glob`, which gets included through `rimraf`, which gets included through `flat-cache`, which gets included through `file-entry-cache`, which gets included through various `eslint` modules, which gets included by `eslint-plugin-react-hooks`, which gets included by `eslint-config-next`.

So actually, an even smaller reproducer would be to just install `eslint-plugin-react-hooks`:

```
❯ npm i -D eslint-plugin-react-hooks
npm warn deprecated inflight@1.0.6: This module is not supported, and leaks memory. Do not use it. Check out lru-cache if you want a good and tested way to coalesce async requests by a key value, which is much more comprehensive and powerful.
npm warn deprecated rimraf@3.0.2: Rimraf versions prior to v4 are no longer supported
npm warn deprecated glob@7.2.3: Glob versions prior to v9 are no longer supported

added 100 packages in 4s

23 packages are looking for funding
  run `npm fund` for details
```

But there is an upcoming release where this will be fixed. I wrote a comment about this [here](https://github.com/vercel/next.js/issues/66239#issuecomment-2156063885).

It would be nice to get rid of this module from the `eslint` dependency altogether, also from the development dependencies. 

