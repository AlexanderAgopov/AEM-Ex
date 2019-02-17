package com.jazzjack;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;

import org.apache.jackrabbit.commons.cnd.CndImporter;
import org.apache.jackrabbit.commons.cnd.ParseException;
import org.apache.jackrabbit.core.TransientRepository;

public class CustomNodesBuilder {

	public static void main(String[] args)
			throws LoginException, RepositoryException, FileNotFoundException, ParseException, IOException {
		// TODO Auto-generated method stub
		Repository repository = new TransientRepository();
		Session session = repository.login(new SimpleCredentials("admin", "admin".toCharArray()));

		try {
			CndImporter.registerNodeTypes(new FileReader("src/main/resources/article.cnd"), session);
			Node root = session.getRootNode();
			Node articles = root.addNode("articles");
			Node newNode = articles.addNode("article", "ca:article");
			newNode.setProperty("ca:headline", "First heading");
			newNode.setProperty("ca:body", "Hello World!");
			System.out.println(newNode.getProperty("ca:headline").getString());
			System.out.println(newNode.getProperty("ca:body").getString());
			session.save();
		} finally {
			session.logout();
		}

	}

}
