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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|console
operator|.
name|CommandContext
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
name|console
operator|.
name|command
operator|.
name|AbstractJmxCommand
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
name|console
operator|.
name|command
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
name|activemq
operator|.
name|console
operator|.
name|formatter
operator|.
name|CommandShellOutputFormatter
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
name|Argument
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * @version $Rev: 960482 $ $Date: 2010-07-05 10:28:33 +0200 (Mon, 05 Jul 2010) $  */
end_comment

begin_class
specifier|public
class|class
name|ActiveMQCommandSupport
extends|extends
name|OsgiCommandSupport
block|{
specifier|private
name|Command
name|command
decl_stmt|;
annotation|@
name|Argument
argument_list|(
name|index
operator|=
literal|0
argument_list|,
name|multiValued
operator|=
literal|true
argument_list|,
name|required
operator|=
literal|true
argument_list|)
specifier|private
name|ArrayList
argument_list|<
name|String
argument_list|>
name|arguments
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|Object
name|doExecute
parameter_list|()
throws|throws
name|Exception
block|{
name|CommandContext
name|context2
init|=
operator|new
name|CommandContext
argument_list|()
decl_stmt|;
name|context2
operator|.
name|setFormatter
argument_list|(
operator|new
name|CommandShellOutputFormatter
argument_list|(
name|System
operator|.
name|out
argument_list|)
argument_list|)
expr_stmt|;
name|Command
name|currentCommand
init|=
name|command
operator|.
name|getClass
argument_list|()
operator|.
name|newInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|currentCommand
operator|.
name|setCommandContext
argument_list|(
name|context2
argument_list|)
expr_stmt|;
comment|// must be added first
if|if
condition|(
name|command
operator|instanceof
name|AbstractJmxCommand
condition|)
block|{
name|arguments
operator|.
name|add
argument_list|(
literal|0
argument_list|,
literal|"--jmxlocal"
argument_list|)
expr_stmt|;
block|}
name|currentCommand
operator|.
name|execute
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|Throwable
name|cur
init|=
name|e
decl_stmt|;
while|while
condition|(
name|cur
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cur
operator|=
name|cur
operator|.
name|getCause
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|cur
operator|instanceof
name|java
operator|.
name|net
operator|.
name|ConnectException
condition|)
block|{
name|context2
operator|.
name|print
argument_list|(
literal|"\n"
operator|+
literal|"Could not connect to JMX server.  This command requires that the remote JMX server be enabled.\n"
operator|+
literal|"This is typically done by adding the following JVM arguments: \n"
operator|+
literal|"   -Dcom.sun.management.jmxremote.port=1099 -Dcom.sun.management.jmxremote.authenticate=false \n"
operator|+
literal|"   -Dcom.sun.management.jmxremote.ssl=false \n"
operator|+
literal|"\n"
operator|+
literal|"The connection error was: "
operator|+
name|cur
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|e
operator|instanceof
name|Exception
condition|)
block|{
throw|throw
operator|(
name|Exception
operator|)
name|e
throw|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|Command
name|getCommand
parameter_list|()
block|{
return|return
name|command
return|;
block|}
specifier|public
name|void
name|setCommand
parameter_list|(
name|Command
name|command
parameter_list|)
block|{
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
specifier|public
specifier|static
name|String
index|[]
name|toStringArray
parameter_list|(
name|Object
name|args
index|[]
parameter_list|)
block|{
name|String
name|strings
index|[]
init|=
operator|new
name|String
index|[
name|args
operator|.
name|length
index|]
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
name|args
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|strings
index|[
name|i
index|]
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|args
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|strings
return|;
block|}
block|}
end_class

end_unit

