package com.jazzjack;

import java.io.FileInputStream;
import java.io.IOException;

import javax.jcr.ImportUUIDBehavior;
import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.jackrabbit.core.TransientRepository;

public class QueryBuilder {

	public static void main(String[] args) throws LoginException, RepositoryException, IOException {
		// TODO Auto-generated method stub
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
			}

			QueryManager manager = session.getWorkspace().getQueryManager();
			Query query = manager.createQuery("SELECT* FROM [nt:unstructured] where[genre]='Computer'", Query.JCR_SQL2);
			QueryResult result = query.execute();
			NodeIterator nodes = result.getNodes();
			while (nodes.hasNext()) {
				Node node = nodes.nextNode();
				Property property = node.getProperty("title");
				System.out.println(property.getString());
			}

		} finally {
			session.logout();
		}
	}

}
