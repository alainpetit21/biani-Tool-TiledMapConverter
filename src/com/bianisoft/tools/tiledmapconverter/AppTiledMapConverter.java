package com.bianisoft.tools.tiledmapconverter;

import java.io.IOException;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import org.xml.sax.*;
import java.io.*;



public class AppTiledMapConverter {
	class MyContentHandler implements ContentHandler {
		public MyContentHandler()	{	}

		public void characters(char[] text, int start, int length)	throws SAXException	{	}
		public void setDocumentLocator(Locator locator)	{	}
		public void startDocument()	{	}
		public void endDocument()	{	}
		public void startPrefixMapping(String prefix, String uri)	{	}
		public void endPrefixMapping(String prefix)	{	}
		public void startElement(String p_stNamespaceURI, String p_stLocalName, String p_stQualifiedName, Attributes p_atts){
			if(p_stLocalName.equals("map")){

				m_nMapWidth= Integer.parseInt(p_atts.getValue("width"));
				m_nMapHeight= Integer.parseInt(p_atts.getValue("height"));
				m_nTileSize= Integer.parseInt(p_atts.getValue("tilewidth"));

				//TODO : some error checking .. nTileSize, width == height
				//TODO : some error checking .. 1 layer

				m_arMap= new int[m_nMapWidth][m_nMapHeight];

				System.out.print("\nMap Element found!");
				System.out.print("\n\tWidth: " + m_nMapWidth);
				System.out.print("\n\tHeight: " + m_nMapHeight);
				System.out.print("\n\tTileSize: " + m_nTileSize);

			}else if(p_stLocalName.equals("image")){
				int width= Integer.parseInt(p_atts.getValue("width"));
				int height= Integer.parseInt(p_atts.getValue("height"));

				m_nbTilesInBank= (width / m_nTileSize) * (height / m_nTileSize);

				System.out.print("\nImage Element found!");
				System.out.print("\n\tNb Tiles in bank: " + m_nbTilesInBank);
			}else if(p_stLocalName.equals("tile")){
				m_arMap[m_nCptX][m_nCptY]= Integer.parseInt(p_atts.getValue("gid")) - 1;

				if((++m_nCptX) >= m_nMapWidth){
					m_nCptX= 0;
					++m_nCptY;
				}
				System.out.print(".");
			}
		}
		public void endElement(String namespaceURI, String localName, String qualifiedName)	{	}
		public void ignorableWhitespace(char[] text, int start, int length) throws SAXException	{	}
		public void processingInstruction(String target, String data)	{	}
		public void skippedEntity(String name)	{	}
	}


	String	m_stFilenameIn;
	String	m_stFilenameOut;

	int		m_nTileSize;
	int		m_nbTilesInBank;
	int		m_nMapWidth;
	int		m_nMapHeight;


	int[][]	m_arMap;
	int		m_nCptX;
	int		m_nCptY;

	public AppTiledMapConverter(String p_stTiledMapIn){
		m_stFilenameIn= p_stTiledMapIn;

		System.out.print("\nStarting Conversion of Tiled map " + m_stFilenameIn);
	}

	public void doRead() throws SAXException, IOException{
		System.out.print("\nStarting Reading" + m_stFilenameIn);

		XMLReader parser = XMLReaderFactory.createXMLReader();
		parser.setContentHandler(new MyContentHandler());
		parser.parse(m_stFilenameIn);
		
		System.out.println(m_stFilenameIn + " is well-formed.");
	}

	public void doManage() throws SAXException, IOException{
		m_stFilenameOut= m_stFilenameIn.substring(0, m_stFilenameIn.indexOf(".tmx")) + ".map";
	}

	public void doSave() throws IOException{
		System.out.print("\nSaving " + m_stFilenameOut);

		FileOutputStream file	= new FileOutputStream(m_stFilenameOut);
		DataOutputStream dos	= new DataOutputStream(file);

		//Polygons
		dos.writeInt(m_nTileSize);
		dos.writeInt(m_nbTilesInBank);
		dos.writeInt(m_nMapWidth);
		dos.writeInt(m_nMapHeight);

		for(int j= 0; j < m_nMapHeight; ++j){
			for(int i= 0; i < m_nMapWidth; ++i){
				dos.writeInt(m_arMap[i][j]);
			}
		}

		dos.close();
		file.close();
	}
	
    public static void main(String[] args){
		String	stFilename;

		if(args.length < 1){
			JFileChooser fc= new JFileChooser(".");
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Tiled! map file", "tmx");

			fc.setFileFilter(filter);
			if(fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION)
				return;

			stFilename= fc.getSelectedFile().getAbsolutePath();
		}else{
			stFilename= args[0];
		}

		try{
			AppTiledMapConverter app= new AppTiledMapConverter(stFilename);
			app.doRead();
			app.doManage();
			app.doSave();
		}catch(Exception e){
			System.out.print(e);
		}
    }
}
