# CoffeeTime
A static site generator written in Java

This project is WIP, and pretty janky I guess. Written in an endeavour to practice a bit of Java, so don't expect too much.

## How to use
If you really want to try it out, build the jar with maven. (`$ mvn package`).<br>

**Following commands are available:**<br>
### Generation
+ `generate site <site-name>`<br> 
Creates a new directory called site-name and generates a few directories, layout and markdown files, and a `config.yml` into it.<br>
+ `generate post <title-of-my-new-post>`<br>
Creates a new file called title-of-my-new-post.md in your `_posts` directory.
+ `generate page <title-of-my-new-page>`<br> 
Creates a new fine called title-of-my-new-page.md in your `_page` directory.

### Building
+ `build`<br>
Builds the site for you and puts the output into your `_site` folder.

## Actually writing stuff
Generate new posts with the aforementioned `generate post` command, for example `generate post my-new-blog`.
This will create a new markdown file inside your `_posts` directory with an attached frontmatter at the top.
A title based on your filename and the date you generated the file at are automatically generated.
You can also define additional variables inside the frontmatter that you can access inside your layout files.
Other than that, type away your posts in markdown.

`generate page` will create a page for your website. 
It differs from a post in that it will end up on the websites root (example.com/my-post.html) if you didn't set anything different inside your `config.yml`
Pages are also not available in a list like posts are.

## Building your layout
This generator relies on [Apache FreeMarker](https://freemarker.apache.org).
You can read up how to use the templating language in their [manual](https://freemarker.apache.org/docs/index.html).
But here are some of the basics and specific stuff for *CoffeeTime*.

### Accessing Variables
Inside your html layout files you can access variables you have defined inside the frontmatter of your posts, pages and of your config file.
Variables can be accessed by writing the variable name inside `${}` as in `${site.title}`.
The default post variables `date`, `title` and `relativeLink` can be accessed by using `post.title`, `post.date` and so forth.
Note for dates: Refer to the [manual page regarding dates](https://freemarker.apache.org/docs/ref_builtins_date.html) because just writing `${post.date}` itself most probably won't work.

Custom variables can be accessed by using the `post.vars` prefix.
Say, your frontmatter has the line `myvar: test` in it.
In your layout file, you can access it by writing `${post.vars.myvar}`
Accessing page variables works the same as for posts, but it uses `page` instead of `post` inside variable names.

Site variables of your config can be accessed by using `site` as in `site.title` or `site.baseUrl`

A list of posts is saved inside a variable called `posts`.
To list individual posts (or other lists for that matter), you can use this snippet:
```html
<#list posts?reverse as p>
<a href="${p.relativeLink}">${p.title}</a>
<span>${p.date?string["yyyy-MM-dd"]}</span>
</#list>
```

### Partials
You can use FreeMarkers functionality to include macros inside layout files.
By defining a macro that simply outputs html code, you can basically have partials.

For example:
In `_partials.html` we define following macros:
```html
<#macro header>
<header>
    <nav>
        <ul class="nav-menu">
            <li class="nav-item"><span class="nav-title">${site.title}</span></li>
            <li class="nav-item"><a href="${site.baseUrl}">Home</a></li>
            <li class="nav-item"><a href="/about.html">About</a></li>
            <li class="nav-item"><a href="/contact.html">Contact</a></li>
        </ul class="menu">  
    </nav>
</header>
</#macro>

<#macro footer>
<footer>
    <hr>
    <div class="footer-text">
    This site was made with <a href="https://github.com/mikulex/coffeetime">CoffeeTime</a>
    </div>
</footer>
</#macro>
```
We can include the `_partials.html` inside `posts.html` at the very top of the file with `<#import "/_partials.html" as part>`
and call out macros `header` and `footer` by writing `<@part.header/>` and `<@part.header/>`
```html
<#import "/_partials.html" as part>
<!-- ... -->
<body>
    <@part.header/>
     <p> Hello! </p>
    <@part.footer/>
</body>
```
For more complex stuff, check out the [manual](https://freemarker.apache.org/docs/dgui_misc_userdefdir.html) again, because macros are pretty powerful.

### Assets
In a directory called `assets` you can put in JavaScript, CSS and a bunch of other files in it. 
Those get just copied over into the `_site` folder.
Accessing a CSS file inside assets would then for example work like this:
```html
<link rel="stylesheet" href="assets/style.css">
```

# TODO:
Bunch of stuff is still not working up to par, or pretty inefficient.

+ Better error handling
+ Replace and delete only necessary files instead of wiping and copying everytime
