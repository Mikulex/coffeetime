# CoffeeTime
A static site generator written in Java

This project is WIP, and pretty janky I guess. Written in an endeavour to practice a bit of Java, so don't expect too much.

## How to use
If you really want to try it out, build the jar with maven. (`$ maven package`).<br>

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
*WIP* 
