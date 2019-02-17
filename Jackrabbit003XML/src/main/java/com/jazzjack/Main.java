package com.jazzjack;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.jcr.ImportUUIDBehavior;
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

public class Main {

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

	public static void main(String[] args) throws LoginException, RepositoryException, IOException {
		Repository repository = new TransientRepository();
		Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

		try {
			Node root = session.getRootNode();
			if (!root.hasNode("bookstore")) {
				Node node = root.addNode("bookstore", "nt:unstructured");
				FileInputStream fis = new FileInputStream("src/main/resources/test.xml");
				session.importXML(node.getPath(), fis, ImportUUIDBehavior.IMPORT_UUID_CREATE_NEW);
				fis.close();
				session.save();
			} else {
				FileOutputStream fos = new FileOutputStream("src/main/resources/fulldump.xml");
				session.exportDocumentView("/bookstore", fos, true, false);
				// if we want to export the system view we should use:
				// session.exportSystemView("/bookstore", fos, true, false);
				fos.close();
			}
			dumpToConsole(root);
		} finally {
			session.logout();
		}
	}

}
