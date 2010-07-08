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
name|karaf
operator|.
name|commands
package|;
end_package

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
name|FileOutputStream
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
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Scanner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|gogo
operator|.
name|commands
operator|.
name|Option
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|felix
operator|.
name|gogo
operator|.
name|commands
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|karaf
operator|.
name|shell
operator|.
name|console
operator|.
name|OsgiCommandSupport
import|;
end_import

begin_comment
comment|/**  * @version $Rev: 960482 $ $Date: 2010-07-05 10:28:33 +0200 (Mon, 05 Jul 2010) $  */
end_comment

begin_class
annotation|@
name|Command
argument_list|(
name|scope
operator|=
literal|"activemq"
argument_list|,
name|name
operator|=
literal|"create-broker"
argument_list|,
name|description
operator|=
literal|"Creates a broker instance."
argument_list|)
specifier|public
class|class
name|CreateBrokerCommand
extends|extends
name|OsgiCommandSupport
block|{
annotation|@
name|Option
argument_list|(
name|name
operator|=
literal|"-n"
argument_list|,
name|aliases
operator|=
block|{
literal|"--name"
block|}
argument_list|,
name|description
operator|=
literal|"The name of the broker (defaults to localhost)."
argument_list|)
specifier|private
name|String
name|name
init|=
literal|"localhost"
decl_stmt|;
comment|/*      * (non-Javadoc)      * @see      * org.apache.karaf.shell.console.OsgiCommandSupport#doExecute()      */
specifier|protected
name|Object
name|doExecute
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|String
name|name
init|=
name|getName
argument_list|()
decl_stmt|;
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"karaf.base"
argument_list|)
argument_list|)
decl_stmt|;
name|File
name|deploy
init|=
operator|new
name|File
argument_list|(
name|base
argument_list|,
literal|"deploy"
argument_list|)
decl_stmt|;
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|props
operator|.
name|put
argument_list|(
literal|"${name}"
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|mkdir
argument_list|(
name|deploy
argument_list|)
expr_stmt|;
name|File
name|configFile
init|=
operator|new
name|File
argument_list|(
name|deploy
argument_list|,
name|name
operator|+
literal|"-broker.xml"
argument_list|)
decl_stmt|;
name|copyFilteredResourceTo
argument_list|(
name|configFile
argument_list|,
literal|"broker.xml"
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Default ActiveMQ Broker ("
operator|+
name|name
operator|+
literal|") configuration file created at: "
operator|+
name|configFile
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Please review the configuration and modify to suite your needs.  "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
return|return
literal|0
return|;
block|}
specifier|private
name|void
name|copyFilteredResourceTo
parameter_list|(
name|File
name|outFile
parameter_list|,
name|String
name|resource
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
operator|!
name|outFile
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating file: @|green "
operator|+
name|outFile
operator|.
name|getPath
argument_list|()
operator|+
literal|"|"
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
name|CreateBrokerCommand
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
name|resource
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Read it line at a time so that we can use the platform line
comment|// ending when we write it out.
name|PrintStream
name|out
init|=
operator|new
name|PrintStream
argument_list|(
operator|new
name|FileOutputStream
argument_list|(
name|outFile
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|Scanner
name|scanner
init|=
operator|new
name|Scanner
argument_list|(
name|is
argument_list|)
decl_stmt|;
while|while
condition|(
name|scanner
operator|.
name|hasNextLine
argument_list|()
condition|)
block|{
name|String
name|line
init|=
name|scanner
operator|.
name|nextLine
argument_list|()
decl_stmt|;
name|line
operator|=
name|filter
argument_list|(
name|line
argument_list|,
name|props
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
name|line
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|safeClose
argument_list|(
name|out
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|safeClose
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"@|red File already exists|. Move it out of the way if you want it re-created: "
operator|+
name|outFile
operator|.
name|getPath
argument_list|()
operator|+
literal|""
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|safeClose
parameter_list|(
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|is
operator|==
literal|null
condition|)
return|return;
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{         }
block|}
specifier|private
name|void
name|safeClose
parameter_list|(
name|OutputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|is
operator|==
literal|null
condition|)
return|return;
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ignore
parameter_list|)
block|{         }
block|}
specifier|private
name|String
name|filter
parameter_list|(
name|String
name|line
parameter_list|,
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|props
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|i
range|:
name|props
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|int
name|p1
decl_stmt|;
while|while
condition|(
operator|(
name|p1
operator|=
name|line
operator|.
name|indexOf
argument_list|(
name|i
operator|.
name|getKey
argument_list|()
argument_list|)
operator|)
operator|>=
literal|0
condition|)
block|{
name|String
name|l1
init|=
name|line
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p1
argument_list|)
decl_stmt|;
name|String
name|l2
init|=
name|line
operator|.
name|substring
argument_list|(
name|p1
operator|+
name|i
operator|.
name|getKey
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|line
operator|=
name|l1
operator|+
name|i
operator|.
name|getValue
argument_list|()
operator|+
name|l2
expr_stmt|;
block|}
block|}
return|return
name|line
return|;
block|}
specifier|private
name|void
name|mkdir
parameter_list|(
name|File
name|file
parameter_list|)
block|{
if|if
condition|(
operator|!
name|file
operator|.
name|exists
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating missing directory: @|green "
operator|+
name|file
operator|.
name|getPath
argument_list|()
operator|+
literal|"|"
argument_list|)
expr_stmt|;
name|file
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
name|File
name|base
init|=
operator|new
name|File
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"karaf.base"
argument_list|)
argument_list|)
decl_stmt|;
name|name
operator|=
name|base
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
block|}
end_class

end_unit

