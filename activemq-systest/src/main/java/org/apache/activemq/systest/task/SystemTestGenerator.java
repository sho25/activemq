begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|systest
operator|.
name|task
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|systest
operator|.
name|DestinationFactory
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|systest
operator|.
name|QueueOnlyScenario
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|systest
operator|.
name|Scenario
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|systest
operator|.
name|ScenarioTestSuite
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|systest
operator|.
name|TopicOnlyScenario
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|DirectoryScanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|codehaus
operator|.
name|jam
operator|.
name|JClass
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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

begin_comment
comment|/**  *   * @version $Revision: 1.1 $  */
end_comment

begin_class
specifier|public
class|class
name|SystemTestGenerator
block|{
specifier|private
name|JClass
index|[]
name|classes
decl_stmt|;
specifier|private
specifier|final
name|File
name|destDir
decl_stmt|;
specifier|private
specifier|final
name|DirectoryScanner
name|clientsScanner
decl_stmt|;
specifier|private
specifier|final
name|DirectoryScanner
name|brokersScanner
decl_stmt|;
specifier|private
specifier|final
name|File
name|baseDir
decl_stmt|;
specifier|private
specifier|final
name|File
name|scenariosFile
decl_stmt|;
specifier|private
name|String
name|licenseHeader
decl_stmt|;
specifier|public
name|SystemTestGenerator
parameter_list|(
name|JClass
index|[]
name|classes
parameter_list|,
name|File
name|destDir
parameter_list|,
name|DirectoryScanner
name|clientsScanner
parameter_list|,
name|DirectoryScanner
name|brokersScanner
parameter_list|,
name|File
name|baseDir
parameter_list|,
name|File
name|scenariosFile
parameter_list|)
block|{
name|this
operator|.
name|classes
operator|=
name|classes
expr_stmt|;
name|this
operator|.
name|destDir
operator|=
name|destDir
expr_stmt|;
name|this
operator|.
name|clientsScanner
operator|=
name|clientsScanner
expr_stmt|;
name|this
operator|.
name|brokersScanner
operator|=
name|brokersScanner
expr_stmt|;
name|this
operator|.
name|baseDir
operator|=
name|baseDir
expr_stmt|;
name|this
operator|.
name|scenariosFile
operator|=
name|scenariosFile
expr_stmt|;
block|}
specifier|public
name|void
name|generate
parameter_list|()
throws|throws
name|IOException
block|{
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|classes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|JClass
name|type
init|=
name|classes
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|implementsInterface
argument_list|(
name|type
argument_list|,
name|Scenario
operator|.
name|class
argument_list|)
operator|&&
operator|!
name|type
operator|.
name|isAbstract
argument_list|()
operator|&&
operator|!
name|type
operator|.
name|isInterface
argument_list|()
condition|)
block|{
name|generateTestsFor
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
block|}
comment|// now lets generate a list of all the available
if|if
condition|(
name|scenariosFile
operator|!=
literal|null
condition|)
block|{
name|generatePropertiesFile
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|generatePropertiesFile
parameter_list|(
name|List
name|list
parameter_list|)
throws|throws
name|IOException
block|{
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|scenariosFile
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
name|iter
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|JClass
name|type
init|=
operator|(
name|JClass
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|writer
operator|.
name|print
argument_list|(
name|type
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|print
argument_list|(
literal|" = "
argument_list|)
expr_stmt|;
name|writeInterfaces
argument_list|(
name|writer
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|writeInterfaces
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|JClass
name|type
parameter_list|)
block|{
name|List
name|interfaces
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
name|addAllInterfaces
argument_list|(
name|interfaces
argument_list|,
name|type
argument_list|)
expr_stmt|;
name|boolean
name|first
init|=
literal|true
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|interfaces
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|JClass
name|interfaceType
init|=
operator|(
name|JClass
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|first
condition|)
block|{
name|first
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|writer
operator|.
name|print
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|print
argument_list|(
name|interfaceType
operator|.
name|getQualifiedName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|addAllInterfaces
parameter_list|(
name|List
name|list
parameter_list|,
name|JClass
name|type
parameter_list|)
block|{
name|JClass
index|[]
name|interfaces
init|=
name|type
operator|.
name|getInterfaces
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|interfaces
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|JClass
name|interfaceType
init|=
name|interfaces
index|[
name|i
index|]
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|interfaceType
argument_list|)
expr_stmt|;
block|}
name|JClass
name|superclass
init|=
name|type
operator|.
name|getSuperclass
argument_list|()
decl_stmt|;
if|if
condition|(
name|superclass
operator|!=
literal|null
condition|)
block|{
name|addAllInterfaces
argument_list|(
name|list
argument_list|,
name|superclass
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|generateTestsFor
parameter_list|(
name|JClass
name|type
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|files
init|=
name|clientsScanner
operator|.
name|getIncludedFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|clientsScanner
operator|.
name|getBasedir
argument_list|()
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|generateTestsFor
argument_list|(
name|type
argument_list|,
name|name
argument_list|,
name|file
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|generateTestsFor
parameter_list|(
name|JClass
name|type
parameter_list|,
name|String
name|clientName
parameter_list|,
name|File
name|clientFile
parameter_list|)
throws|throws
name|IOException
block|{
name|String
index|[]
name|files
init|=
name|brokersScanner
operator|.
name|getIncludedFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|files
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|name
init|=
name|files
index|[
name|i
index|]
decl_stmt|;
name|File
name|basedir
init|=
name|brokersScanner
operator|.
name|getBasedir
argument_list|()
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|basedir
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|implementsInterface
argument_list|(
name|type
argument_list|,
name|TopicOnlyScenario
operator|.
name|class
argument_list|)
condition|)
block|{
name|generateTestsFor
argument_list|(
name|type
argument_list|,
name|clientName
argument_list|,
name|clientFile
argument_list|,
name|name
argument_list|,
name|file
argument_list|,
name|DestinationFactory
operator|.
name|QUEUE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|implementsInterface
argument_list|(
name|type
argument_list|,
name|QueueOnlyScenario
operator|.
name|class
argument_list|)
condition|)
block|{
name|generateTestsFor
argument_list|(
name|type
argument_list|,
name|clientName
argument_list|,
name|clientFile
argument_list|,
name|name
argument_list|,
name|file
argument_list|,
name|DestinationFactory
operator|.
name|TOPIC
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|void
name|generateTestsFor
parameter_list|(
name|JClass
name|type
parameter_list|,
name|String
name|clientName
parameter_list|,
name|File
name|clientFile
parameter_list|,
name|String
name|brokerName
parameter_list|,
name|File
name|brokerFile
parameter_list|,
name|int
name|destinationType
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|clientPrefix
init|=
name|trimPostFix
argument_list|(
name|clientName
argument_list|)
decl_stmt|;
name|String
name|brokerPrefix
init|=
name|trimPostFix
argument_list|(
name|brokerName
argument_list|)
decl_stmt|;
name|String
name|destinationName
init|=
name|ScenarioTestSuite
operator|.
name|destinationDescription
argument_list|(
name|destinationType
argument_list|)
decl_stmt|;
name|String
index|[]
name|paths
init|=
block|{
literal|"org"
block|,
literal|"activemq"
block|,
literal|"systest"
block|,
name|brokerPrefix
block|,
name|destinationName
block|,
name|clientPrefix
block|}
decl_stmt|;
name|String
name|packageName
init|=
name|asPackageName
argument_list|(
name|paths
argument_list|)
decl_stmt|;
name|File
name|dir
init|=
name|makeDirectories
argument_list|(
name|paths
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|type
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"Test.java"
argument_list|)
decl_stmt|;
name|PrintWriter
name|writer
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Generating: "
operator|+
name|file
argument_list|)
expr_stmt|;
name|generateFile
argument_list|(
name|writer
argument_list|,
name|type
argument_list|,
name|clientFile
argument_list|,
name|brokerFile
argument_list|,
name|packageName
argument_list|,
name|destinationType
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|generateFile
parameter_list|(
name|PrintWriter
name|writer
parameter_list|,
name|JClass
name|type
parameter_list|,
name|File
name|clientFile
parameter_list|,
name|File
name|brokerFile
parameter_list|,
name|String
name|packageName
parameter_list|,
name|int
name|destinationType
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|println
argument_list|(
name|getLicenseHeader
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"package "
operator|+
name|packageName
operator|+
literal|";"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"import org.apache.activemq.systest.DestinationFactory;"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"import org.apache.activemq.systest.ScenarioTestSuite;"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"import "
operator|+
name|type
operator|.
name|getQualifiedName
argument_list|()
operator|+
literal|";"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"import org.springframework.context.ApplicationContext;"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"import org.springframework.context.support.FileSystemXmlApplicationContext;"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"import junit.framework.TestSuite;"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"/**"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|" * System test case for Scenario: "
operator|+
name|type
operator|.
name|getSimpleName
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|" *"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|" *"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|" * NOTE!: This file is auto generated - do not modify!"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|" *        if you need to make a change, please see SystemTestGenerator code"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|" *        in the activemq-systest module in ActiveMQ 4.x"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|" *"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|" * @version $Revision:$"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|" */"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"public class "
operator|+
name|type
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|"Test extends ScenarioTestSuite {"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|()
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"    public static TestSuite suite() throws Exception {"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"        ApplicationContext clientContext = new FileSystemXmlApplicationContext(\""
operator|+
name|relativePath
argument_list|(
name|clientFile
argument_list|)
operator|+
literal|"\");"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"        ApplicationContext brokerContext = new FileSystemXmlApplicationContext(\""
operator|+
name|relativePath
argument_list|(
name|brokerFile
argument_list|)
operator|+
literal|"\");"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"        Class[] scenarios = { "
operator|+
name|type
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|".class };"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"        return createSuite(clientContext, brokerContext, scenarios, "
operator|+
name|destinationExpression
argument_list|(
name|destinationType
argument_list|)
operator|+
literal|");"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"    }"
argument_list|)
expr_stmt|;
name|writer
operator|.
name|println
argument_list|(
literal|"}"
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|destinationExpression
parameter_list|(
name|int
name|destinationType
parameter_list|)
block|{
switch|switch
condition|(
name|destinationType
condition|)
block|{
case|case
name|DestinationFactory
operator|.
name|QUEUE
case|:
return|return
literal|"DestinationFactory.QUEUE"
return|;
default|default:
return|return
literal|"DestinationFactory.TOPIC"
return|;
block|}
block|}
specifier|protected
name|String
name|relativePath
parameter_list|(
name|File
name|file
parameter_list|)
block|{
name|String
name|name
init|=
name|file
operator|.
name|toString
argument_list|()
decl_stmt|;
name|String
name|prefix
init|=
name|baseDir
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
name|prefix
argument_list|)
condition|)
block|{
return|return
name|name
operator|.
name|substring
argument_list|(
name|prefix
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
return|;
block|}
return|return
name|name
return|;
block|}
specifier|protected
name|File
name|makeDirectories
parameter_list|(
name|String
index|[]
name|paths
parameter_list|)
block|{
name|File
name|dir
init|=
name|destDir
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|paths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|dir
operator|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|paths
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
return|return
name|dir
return|;
block|}
specifier|protected
name|String
name|asPackageName
parameter_list|(
name|String
index|[]
name|paths
parameter_list|)
block|{
name|StringBuffer
name|buffer
init|=
operator|new
name|StringBuffer
argument_list|(
name|paths
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|paths
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buffer
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
name|buffer
operator|.
name|append
argument_list|(
name|paths
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|buffer
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|protected
name|String
name|trimPostFix
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|int
name|idx
init|=
name|uri
operator|.
name|lastIndexOf
argument_list|(
literal|'.'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|>
literal|0
condition|)
block|{
return|return
name|uri
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|idx
argument_list|)
return|;
block|}
return|return
name|uri
return|;
block|}
specifier|protected
name|boolean
name|implementsInterface
parameter_list|(
name|JClass
name|type
parameter_list|,
name|Class
name|interfaceClass
parameter_list|)
block|{
name|JClass
index|[]
name|interfaces
init|=
name|type
operator|.
name|getInterfaces
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|interfaces
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|JClass
name|anInterface
init|=
name|interfaces
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|anInterface
operator|.
name|getQualifiedName
argument_list|()
operator|.
name|equals
argument_list|(
name|interfaceClass
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
name|JClass
name|superclass
init|=
name|type
operator|.
name|getSuperclass
argument_list|()
decl_stmt|;
if|if
condition|(
name|superclass
operator|==
literal|null
operator|||
name|superclass
operator|==
name|type
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
return|return
name|implementsInterface
argument_list|(
name|superclass
argument_list|,
name|interfaceClass
argument_list|)
return|;
block|}
block|}
specifier|public
name|String
name|getLicenseHeader
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|licenseHeader
operator|==
literal|null
condition|)
block|{
comment|// read the LICENSE_HEADER.txt into the licenseHeader variable.
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|InputStream
name|is
init|=
name|SystemTestGenerator
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"LICENSE_HEADER.txt"
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|c
decl_stmt|;
while|while
condition|(
operator|(
name|c
operator|=
name|is
operator|.
name|read
argument_list|()
operator|)
operator|>=
literal|0
condition|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
name|licenseHeader
operator|=
operator|new
name|String
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
return|return
name|licenseHeader
return|;
block|}
specifier|public
name|void
name|setLicenseHeader
parameter_list|(
name|String
name|licenseHeader
parameter_list|)
block|{
name|this
operator|.
name|licenseHeader
operator|=
name|licenseHeader
expr_stmt|;
block|}
block|}
end_class

end_unit

