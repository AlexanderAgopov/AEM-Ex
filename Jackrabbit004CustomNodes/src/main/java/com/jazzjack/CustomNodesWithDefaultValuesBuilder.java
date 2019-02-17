package com.jazzjack;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

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

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;

public class CustomNodesWithDefaultValuesBuilder {

	public static void main(String[] args)
			throws LoginException, RepositoryException, FileNotFoundException, ParseException, IOException {
		// TODO Auto-generated method stub
		Repository repository = new TransientRepository();
		Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

		try {
			CndImporter.registerNodeTypes(new FileReader("src/main/resources/articleWithDefaultValues.cnd"), session);
			Node root = session.getRootNode();
			Node articlesDefault = root.addNode("articlesDV");
			Node newNode = articlesDefault.addNode("newNode", "ca:articledv");
			dumpToConsole(articlesDefault);
			session.save();

		} finally {
			session.logout();
		}

	}

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

}
