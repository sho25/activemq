begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|console
operator|.
name|command
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Attr
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathExpressionException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPathFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
specifier|public
class|class
name|CreateCommand
extends|extends
name|AbstractCommand
block|{
specifier|protected
specifier|final
name|String
index|[]
name|helpFile
init|=
operator|new
name|String
index|[]
block|{
literal|"Task Usage: Main create path/to/brokerA [create-options]"
block|,
literal|"Description:  Creates a runnable broker instance in the specified path."
block|,
literal|""
block|,
literal|"List Options:"
block|,
literal|"    --amqconf<file path>   Path to ActiveMQ conf file that will be used in the broker instance. Default is: conf/activemq.xml"
block|,
literal|"    --version               Display the version information."
block|,
literal|"    -h,-?,--help            Display the create broker help information."
block|,
literal|""
block|}
decl_stmt|;
specifier|protected
specifier|final
name|String
name|DEFAULT_TARGET_ACTIVEMQ_CONF
init|=
literal|"conf/activemq.xml"
decl_stmt|;
comment|// default activemq conf to create in the new broker instance
specifier|protected
specifier|final
name|String
name|DEFAULT_BROKERNAME_XPATH
init|=
literal|"/beans/broker/@brokerName"
decl_stmt|;
comment|// default broker name xpath to change the broker name
specifier|protected
specifier|final
name|String
index|[]
name|BASE_SUB_DIRS
init|=
block|{
literal|"bin"
block|,
literal|"conf"
block|}
decl_stmt|;
comment|// default sub directories that will be created
specifier|protected
specifier|final
name|String
name|BROKER_NAME_REGEX
init|=
literal|"[$][{]brokerName[}]"
decl_stmt|;
comment|// use to replace broker name property holders
specifier|protected
name|String
name|amqConf
init|=
literal|"conf/activemq.xml"
decl_stmt|;
comment|// default conf if no conf is specified via --amqconf
comment|// default files to create
specifier|protected
name|String
index|[]
index|[]
name|fileWriteMap
init|=
block|{
block|{
literal|"winActivemq"
block|,
literal|"bin/${brokerName}.bat"
block|}
block|,
block|{
literal|"unixActivemq"
block|,
literal|"bin/${brokerName}"
block|}
block|}
decl_stmt|;
specifier|protected
name|String
name|brokerName
decl_stmt|;
specifier|protected
name|File
name|amqHome
decl_stmt|;
specifier|protected
name|File
name|targetAmqBase
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"create"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOneLineDescription
parameter_list|()
block|{
return|return
literal|"Creates a runnable broker instance in the specified path."
return|;
block|}
specifier|protected
name|void
name|runTask
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
name|context
operator|.
name|print
argument_list|(
literal|"Running create broker task..."
argument_list|)
expr_stmt|;
name|amqHome
operator|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"activemq.home"
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|String
name|token
range|:
name|tokens
control|)
block|{
name|targetAmqBase
operator|=
operator|new
name|File
argument_list|(
name|token
argument_list|)
expr_stmt|;
name|brokerName
operator|=
name|targetAmqBase
operator|.
name|getName
argument_list|()
expr_stmt|;
if|if
condition|(
name|targetAmqBase
operator|.
name|exists
argument_list|()
condition|)
block|{
name|BufferedReader
name|console
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|System
operator|.
name|in
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|resp
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|context
operator|.
name|print
argument_list|(
literal|"Target directory ("
operator|+
name|targetAmqBase
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|") already exists. Overwrite (y/n): "
argument_list|)
expr_stmt|;
name|resp
operator|=
name|console
operator|.
name|readLine
argument_list|()
expr_stmt|;
if|if
condition|(
name|resp
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"y"
argument_list|)
operator|||
name|resp
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
break|break;
block|}
elseif|else
if|if
condition|(
name|resp
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"n"
argument_list|)
operator|||
name|resp
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"no"
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
name|context
operator|.
name|print
argument_list|(
literal|"Creating directory: "
operator|+
name|targetAmqBase
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|targetAmqBase
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|createSubDirs
argument_list|(
name|targetAmqBase
argument_list|,
name|BASE_SUB_DIRS
argument_list|)
expr_stmt|;
name|writeFileMapping
argument_list|(
name|targetAmqBase
argument_list|,
name|fileWriteMap
argument_list|)
expr_stmt|;
name|copyActivemqConf
argument_list|(
name|amqHome
argument_list|,
name|targetAmqBase
argument_list|,
name|amqConf
argument_list|)
expr_stmt|;
name|copyConfDirectory
argument_list|(
operator|new
name|File
argument_list|(
name|amqHome
argument_list|,
literal|"conf"
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|targetAmqBase
argument_list|,
literal|"conf"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Handle the --amqconf options.      *      * @param token  - option token to handle      * @param tokens - succeeding command arguments      * @throws Exception      */
specifier|protected
name|void
name|handleOption
parameter_list|(
name|String
name|token
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|tokens
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|token
operator|.
name|startsWith
argument_list|(
literal|"--amqconf"
argument_list|)
condition|)
block|{
comment|// If no amqconf specified, or next token is a new option
if|if
condition|(
name|tokens
operator|.
name|isEmpty
argument_list|()
operator|||
name|tokens
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|startsWith
argument_list|(
literal|"-"
argument_list|)
condition|)
block|{
name|context
operator|.
name|printException
argument_list|(
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Attributes to amqconf not specified"
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|amqConf
operator|=
name|tokens
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Let super class handle unknown option
name|super
operator|.
name|handleOption
argument_list|(
name|token
argument_list|,
name|tokens
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|createSubDirs
parameter_list|(
name|File
name|target
parameter_list|,
name|String
index|[]
name|subDirs
parameter_list|)
throws|throws
name|IOException
block|{
name|File
name|subDirFile
decl_stmt|;
for|for
control|(
name|String
name|subDir
range|:
name|BASE_SUB_DIRS
control|)
block|{
name|subDirFile
operator|=
operator|new
name|File
argument_list|(
name|target
argument_list|,
name|subDir
argument_list|)
expr_stmt|;
name|context
operator|.
name|print
argument_list|(
literal|"Creating directory: "
operator|+
name|subDirFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|subDirFile
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|writeFileMapping
parameter_list|(
name|File
name|targetBase
parameter_list|,
name|String
index|[]
index|[]
name|fileWriteMapping
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|String
index|[]
name|fileWrite
range|:
name|fileWriteMapping
control|)
block|{
name|File
name|dest
init|=
operator|new
name|File
argument_list|(
name|targetBase
argument_list|,
name|resolveParam
argument_list|(
name|BROKER_NAME_REGEX
argument_list|,
name|brokerName
argument_list|,
name|fileWrite
index|[
literal|1
index|]
argument_list|)
argument_list|)
decl_stmt|;
name|context
operator|.
name|print
argument_list|(
literal|"Creating new file: "
operator|+
name|dest
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|writeFile
argument_list|(
name|fileWrite
index|[
literal|0
index|]
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|copyActivemqConf
parameter_list|(
name|File
name|srcBase
parameter_list|,
name|File
name|targetBase
parameter_list|,
name|String
name|activemqConf
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|TransformerException
throws|,
name|XPathExpressionException
block|{
name|File
name|src
init|=
operator|new
name|File
argument_list|(
name|srcBase
argument_list|,
name|activemqConf
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|src
operator|.
name|exists
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"File: "
operator|+
name|src
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|" not found."
argument_list|)
throw|;
block|}
name|File
name|dest
init|=
operator|new
name|File
argument_list|(
name|targetBase
argument_list|,
name|DEFAULT_TARGET_ACTIVEMQ_CONF
argument_list|)
decl_stmt|;
name|context
operator|.
name|print
argument_list|(
literal|"Copying from: "
operator|+
name|src
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|"\n          to: "
operator|+
name|dest
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentBuilderFactory
name|dbf
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|dbf
operator|.
name|setFeature
argument_list|(
name|XMLConstants
operator|.
name|FEATURE_SECURE_PROCESSING
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|dbf
operator|.
name|setFeature
argument_list|(
literal|"http://apache.org/xml/features/disallow-doctype-decl"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|dbf
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Element
name|docElem
init|=
name|builder
operator|.
name|parse
argument_list|(
name|src
argument_list|)
operator|.
name|getDocumentElement
argument_list|()
decl_stmt|;
name|XPath
name|xpath
init|=
name|XPathFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newXPath
argument_list|()
decl_stmt|;
name|Attr
name|brokerNameAttr
init|=
operator|(
name|Attr
operator|)
name|xpath
operator|.
name|evaluate
argument_list|(
name|DEFAULT_BROKERNAME_XPATH
argument_list|,
name|docElem
argument_list|,
name|XPathConstants
operator|.
name|NODE
argument_list|)
decl_stmt|;
name|brokerNameAttr
operator|.
name|setValue
argument_list|(
name|brokerName
argument_list|)
expr_stmt|;
name|writeToFile
argument_list|(
operator|new
name|DOMSource
argument_list|(
name|docElem
argument_list|)
argument_list|,
name|dest
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|printHelp
parameter_list|()
block|{
name|context
operator|.
name|printHelp
argument_list|(
name|helpFile
argument_list|)
expr_stmt|;
block|}
comment|// write the default files to create (i.e. script files)
specifier|private
name|void
name|writeFile
parameter_list|(
name|String
name|typeName
parameter_list|,
name|File
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|data
decl_stmt|;
if|if
condition|(
name|typeName
operator|.
name|equals
argument_list|(
literal|"winActivemq"
argument_list|)
condition|)
block|{
name|data
operator|=
name|winActivemqData
expr_stmt|;
name|data
operator|=
name|resolveParam
argument_list|(
literal|"[$][{]activemq.home[}]"
argument_list|,
name|amqHome
operator|.
name|getCanonicalPath
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[\\\\]"
argument_list|,
literal|"/"
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|data
operator|=
name|resolveParam
argument_list|(
literal|"[$][{]activemq.base[}]"
argument_list|,
name|targetAmqBase
operator|.
name|getCanonicalPath
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[\\\\]"
argument_list|,
literal|"/"
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|typeName
operator|.
name|equals
argument_list|(
literal|"unixActivemq"
argument_list|)
condition|)
block|{
name|data
operator|=
name|getUnixActivemqData
argument_list|()
expr_stmt|;
name|data
operator|=
name|resolveParam
argument_list|(
literal|"[$][{]activemq.home[}]"
argument_list|,
name|amqHome
operator|.
name|getCanonicalPath
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[\\\\]"
argument_list|,
literal|"/"
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
name|data
operator|=
name|resolveParam
argument_list|(
literal|"[$][{]activemq.base[}]"
argument_list|,
name|targetAmqBase
operator|.
name|getCanonicalPath
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"[\\\\]"
argument_list|,
literal|"/"
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Unknown file type: "
operator|+
name|typeName
argument_list|)
throw|;
block|}
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|data
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|buf
operator|.
name|put
argument_list|(
name|data
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|flip
argument_list|()
expr_stmt|;
try|try
init|(
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|dest
argument_list|)
init|;
name|FileChannel
name|destinationChannel
operator|=
name|fos
operator|.
name|getChannel
argument_list|()
init|)
block|{
name|destinationChannel
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
block|}
comment|// Set file permissions available for Java 6.0 only
name|dest
operator|.
name|setExecutable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dest
operator|.
name|setReadable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|dest
operator|.
name|setWritable
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|// utlity method to write an xml source to file
specifier|private
name|void
name|writeToFile
parameter_list|(
name|Source
name|src
parameter_list|,
name|File
name|file
parameter_list|)
throws|throws
name|TransformerException
block|{
name|TransformerFactory
name|tFactory
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|tFactory
operator|.
name|setFeature
argument_list|(
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
operator|.
name|FEATURE_SECURE_PROCESSING
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
name|Transformer
name|fileTransformer
init|=
name|tFactory
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|Result
name|res
init|=
operator|new
name|StreamResult
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|fileTransformer
operator|.
name|transform
argument_list|(
name|src
argument_list|,
name|res
argument_list|)
expr_stmt|;
block|}
comment|// utility method to copy one file to another
specifier|private
name|void
name|copyFile
parameter_list|(
name|File
name|from
parameter_list|,
name|File
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|from
operator|.
name|exists
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
init|(
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|from
argument_list|)
init|;
name|FileChannel
name|sourceChannel
operator|=
name|fis
operator|.
name|getChannel
argument_list|()
init|;
name|FileOutputStream
name|fos
operator|=
operator|new
name|FileOutputStream
argument_list|(
name|dest
argument_list|)
init|;
name|FileChannel
name|destinationChannel
operator|=
name|fos
operator|.
name|getChannel
argument_list|()
init|)
block|{
name|sourceChannel
operator|.
name|transferTo
argument_list|(
literal|0
argument_list|,
name|sourceChannel
operator|.
name|size
argument_list|()
argument_list|,
name|destinationChannel
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|copyConfDirectory
parameter_list|(
name|File
name|from
parameter_list|,
name|File
name|dest
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|from
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|String
name|files
index|[]
init|=
name|from
operator|.
name|list
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|file
range|:
name|files
control|)
block|{
name|File
name|srcFile
init|=
operator|new
name|File
argument_list|(
name|from
argument_list|,
name|file
argument_list|)
decl_stmt|;
if|if
condition|(
name|srcFile
operator|.
name|isFile
argument_list|()
operator|&&
operator|!
name|srcFile
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"activemq.xml"
argument_list|)
condition|)
block|{
name|File
name|destFile
init|=
operator|new
name|File
argument_list|(
name|dest
argument_list|,
name|file
argument_list|)
decl_stmt|;
name|context
operator|.
name|print
argument_list|(
literal|"Copying from: "
operator|+
name|srcFile
operator|.
name|getCanonicalPath
argument_list|()
operator|+
literal|"\n          to: "
operator|+
name|destFile
operator|.
name|getCanonicalPath
argument_list|()
argument_list|)
expr_stmt|;
name|copyFile
argument_list|(
name|srcFile
argument_list|,
name|destFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|from
operator|+
literal|" is not a directory"
argument_list|)
throw|;
block|}
block|}
comment|// replace a property place holder (paramName) with the paramValue
specifier|private
name|String
name|resolveParam
parameter_list|(
name|String
name|paramName
parameter_list|,
name|String
name|paramValue
parameter_list|,
name|String
name|target
parameter_list|)
block|{
return|return
name|target
operator|.
name|replaceAll
argument_list|(
name|paramName
argument_list|,
name|paramValue
argument_list|)
return|;
block|}
comment|// Embedded windows script data
specifier|private
specifier|static
specifier|final
name|String
name|winActivemqData
init|=
literal|"@echo off\n"
operator|+
literal|"set ACTIVEMQ_HOME=\"${activemq.home}\"\n"
operator|+
literal|"set ACTIVEMQ_BASE=\"${activemq.base}\"\n"
operator|+
literal|"\n"
operator|+
literal|"set PARAM=%1\n"
operator|+
literal|":getParam\n"
operator|+
literal|"shift\n"
operator|+
literal|"if \"%1\"==\"\" goto end\n"
operator|+
literal|"set PARAM=%PARAM% %1\n"
operator|+
literal|"goto getParam\n"
operator|+
literal|":end\n"
operator|+
literal|"\n"
operator|+
literal|"%ACTIVEMQ_HOME%/bin/activemq %PARAM%"
decl_stmt|;
specifier|private
name|String
name|getUnixActivemqData
parameter_list|()
block|{
name|StringBuffer
name|res
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"#!/bin/sh\n\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"## Figure out the ACTIVEMQ_BASE from the directory this script was run from\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"PRG=\"$0\"\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"progname=`basename \"$0\"`\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"saveddir=`pwd`\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"# need this for relative symlinks\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"dirname_prg=`dirname \"$PRG\"`\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"cd \"$dirname_prg\"\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"while [ -h \"$PRG\" ] ; do\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"  ls=`ls -ld \"$PRG\"`\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"  link=`expr \"$ls\" : '.*-> \\(.*\\)$'`\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"  if expr \"$link\" : '.*/.*'> /dev/null; then\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"    PRG=\"$link\"\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"  else\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"    PRG=`dirname \"$PRG\"`\"/$link\"\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"  fi\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"done\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"ACTIVEMQ_BASE=`dirname \"$PRG\"`/..\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"cd \"$saveddir\"\n\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"ACTIVEMQ_BASE=`cd \"$ACTIVEMQ_BASE\"&& pwd`\n\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"## Enable remote debugging\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"#export ACTIVEMQ_DEBUG_OPTS=\"-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005\"\n\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"## Add system properties for this instance here (if needed), e.g\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"#export ACTIVEMQ_OPTS_MEMORY=\"-Xms256M -Xmx1G\"\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"#export ACTIVEMQ_OPTS=\"$ACTIVEMQ_OPTS_MEMORY -Dorg.apache.activemq.UseDedicatedTaskRunner=true -Djava.util.logging.config.file=logging.properties\"\n\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"export ACTIVEMQ_HOME=${activemq.home}\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"export ACTIVEMQ_BASE=$ACTIVEMQ_BASE\n\n"
argument_list|)
expr_stmt|;
name|res
operator|.
name|append
argument_list|(
literal|"${ACTIVEMQ_HOME}/bin/activemq \"$@\""
argument_list|)
expr_stmt|;
return|return
name|res
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

