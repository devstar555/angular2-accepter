![image](https://codeship.com/projects/34df5270-9487-0134-fd74-3643c9f33b1e/status?branch=master)

# General

## Purpose

The accepter application provides a service that allows to check if an order can be automatically processed and fulfilled or if the order needs special treatment like a manual order review. The dodax backend (see github.com/dodax/backend) downloads orders periodically (multiple times an hour) from platforms and asks the accepter service whether an order can be fulfilled without manual interaction or not.

As each manual check of an order is costly (labor costs), a general goal, which needs to be kept in mind when doing any changes on the accepter service, is to flag as little orders as possible for special treatment, but as many as required to keep bad orders out of the system.

## Workflow

Below is a chart that describes the steps (related to the accepter project) of the order handling workflow at dodax:

![image](https://cloud.githubusercontent.com/assets/1025174/20132783/ddd559aa-a664-11e6-83f0-70e56747160b.png)

When an order is imported by the backend, it is in state NEW. From state NEW, the accepter application will be invoked (green diamond with label "Order Accept"). If the result of the decision by the accepter is ACCEPT, then the order state in the backend will be changed to ACCEPTED, which means that the backend can continue with further steps (like ordering, fulfilling, etc). If the result of the decision by the accepter is REVIEW, the order is routed to a human for manual review. The human will investigate the order and make his own decision whether or not the order is problematic. If the order is considered problematic, it will be forwarded to a process called DELEGATION, in which the order will be fulfilled with special treatment outside of the dodax system (manually). If the delegation is successful, the order state will be changed to DELEGATED (terminal state). If the delegation is not successful, then the state is changed to REJECTED. Let's assume the accepter decided REVIEW and the human being said the order is good (which is the opposite of the accepter decision), then another human being needs to check the order (to make sure there are no mistakes) and if the second person as well decides that the order is fine, then the order is put into the ACCEPTED state, which means that the backend will process it normally, like if it would have been ACCEPTED already by the accepter application.

## Dodax Backend

Once the dodax backend imported an order from a platform, it is available for further processing steps. One of this steps is to order the products from a supplier or to assign an on stock item to the order which will be picked and shipped by the warehouse workers. Before any of this steps is done, the backend will call the accepter application to make sure the order can be automatically fulfilled.

An order in the backend system looks like this:

![image](https://cloud.githubusercontent.com/assets/1025174/20132448/56ff91e4-a663-11e6-992c-9603d31acd82.png)

# Development
## Clone Code from GitHub

First step is to clone the code from the github repository. You can clone the repository to any directory on your machine, there are no restrictions.

## Launch Applications in Development Mode

To launch the applications in Development mode, you need to run two scripts (open two terminals and run each of the commands in one of them):

 * ```play.sh``` This script launches the activator and by default runs the command ```~run``` in it, which will make activator compile the play project and launch a server which is listening on port 9000. Also activator will watch the file system for any changes and recompile immediately once a change is detected. The script will tell docker to bind the container port 9000 to your local machine's port 9000, so you can reach the application at http://127.0.0.1:9000 or if you are using IPv6 [http://[::1]:9000](http://[::1]:9000).

 * ```angular.sh``` This script launches the NG CLI with the command ```serve```, which will make it package the code and start a web server listening on port 4200 for incoming HTTP requests. Also the CLI watches the file system for any changes and starts a repackaging run once a file change was detected. The script tells docker to bind the container port 4200 to your local machine's port 4200, so you can reach the application at http://127.0.0.1:4200/ or if you are using IPv6 [http://[::1]:4200](http://[::1]:4200). The NG CLI is started with the parameter ```--proxy ...``` which sets up a proxy that forwards the API requests to the play container. So if you want to develop on angular and use the API, everything is ready to go.

## Import Code to Eclipse

If you want to import the code to eclipse, you need to run ```./play.sh activator clean compile eclipse```. This will start the activator and launch the eclipse command, which generates the eclipse project files. Once that's done, you can use the *Import Existing Project* functionality of eclipse. The generated eclipse project files contain references to the required libraries, so you should not get any compile errors after importing.

## Package Application for Production

To package the application for production and run it afterwards, use the following commands:

```
./package.sh
docker run -ti accepter-app
```

To run the package script, you need the ```jet``` command to be available on your machine. If you don't have it installed, use the following commands to do so:

```
curl -SLO "https://s3.amazonaws.com/codeship-jet-releases/1.14.5/jet-linux_amd64_1.14.5.tar.gz"
sudo tar -xaC /usr/local/bin -f jet-linux_amd64_1.14.5.tar.gz
sudo chmod +x /usr/local/bin/jet
```
