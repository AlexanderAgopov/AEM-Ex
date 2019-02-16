package com.jazzjack;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Value;

import org.apache.jackrabbit.core.TransientRepository;

public class App {

	// This method prints the node passed as an argument and all its sub-nodes to
	// the
	// console
	public static void dumpToConsole(Node node) throws RepositoryException {
		System.out.println(node.getPath());
		if (node.getName().equals("jcr:system") || node.getName().equals("rep:policy")) {
			return;
		}

		PropertyIterator props = node.getProperties();

		while (props.hasNext()) {
			Property property = (Property) props.next();

			if (property.isMultiple()) {
				Value[] values = property.getValues();
				System.out.println(property.getPath() + "[");

				for (Value v : values) {
					System.out.print(v.toString() + ", ");
				}
				System.out.print("]");
			} else {
				System.out.println(property.getPath() + property.getString());
			}
		}

		NodeIterator iterator = node.getNodes();
		while (iterator.hasNext()) {
			Node nextNode = iterator.nextNode();
			dumpToConsole(nextNode);
		}
	}

	public static void main(String[] args) throws LoginException, RepositoryException {

		Repository repository = new TransientRepository();
		Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));
		System.out.println("Hello World!");

		try {
			Node root = session.getRootNode();

			/*
			 * Node n1 = root.addNode("node1"); Node n2 = n1.addNode("node2");
			 * n2.setProperty("message", "Hello World!"); session.save();
			 * 
			 * The above code does the same thing as the following:
			 */

			root.addNode("nodeA").addNode("nodeB").setProperty("message", "Hello World!");
			session.save();

			// getting a node
			Node selectedNode = root.getNode("nodeA/nodeB");
			// Node selectedNode = session.getNode("nodeA/nodeB"); the result will be the
			// same as the above statement
			System.out.println(selectedNode.getPath());
			System.out.println(selectedNode.getProperty("message").getString());

			Node nodeC = root.addNode("nodeC");
			nodeC.setProperty("msg", "Hi, everyone!");
			Property property = nodeC.getProperty("msg");
			String msg = property.toString();
			// the above code is shortcut for the following
			Value propertyValue = property.getValue();
			String msg1 = propertyValue.toString();

			Node nodeD = nodeC.addNode("nodeD");
			System.out.println(nodeD.getPath());

			dumpToConsole(root);

			// remove a node
			// root.getNode("nodeC/nodeD").remove();
			session.save();

			String vendor = repository.getDescriptor(Repository.REP_VENDOR_DESC);
			String product = repository.getDescriptor(Repository.REP_NAME_DESC);
			String version = repository.getDescriptor(Repository.REP_VERSION_DESC);
			System.out.printf("Vendor: %s%nProduct: %s%nVersion: %s%n", vendor, product, version);
		} finally {
			session.logout();
		}

	}

}
