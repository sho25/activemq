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
name|openwire
operator|.
name|tool
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
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|BuildException
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
name|Project
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
name|Task
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
name|JamService
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
name|JamServiceFactory
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
name|JamServiceParams
import|;
end_import

begin_comment
comment|/**  * @version $Revision: 384826 $  */
end_comment

begin_class
specifier|public
class|class
name|JavaGeneratorTask
extends|extends
name|Task
block|{
name|int
name|version
init|=
literal|2
decl_stmt|;
name|File
name|basedir
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|Project
name|project
init|=
operator|new
name|Project
argument_list|()
decl_stmt|;
name|project
operator|.
name|init
argument_list|()
expr_stmt|;
name|JavaGeneratorTask
name|generator
init|=
operator|new
name|JavaGeneratorTask
argument_list|()
decl_stmt|;
name|generator
operator|.
name|setProject
argument_list|(
name|project
argument_list|)
expr_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|generator
operator|.
name|version
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|1
condition|)
block|{
name|generator
operator|.
name|basedir
operator|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|generator
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
try|try
block|{
name|String
name|sourceDir
init|=
name|basedir
operator|+
literal|"/src/main/java"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Parsing source files in: "
operator|+
name|sourceDir
argument_list|)
expr_stmt|;
name|JamServiceFactory
name|jamServiceFactory
init|=
name|JamServiceFactory
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|JamServiceParams
name|params
init|=
name|jamServiceFactory
operator|.
name|createServiceParams
argument_list|()
decl_stmt|;
name|File
index|[]
name|dirs
init|=
operator|new
name|File
index|[]
block|{
operator|new
name|File
argument_list|(
name|sourceDir
argument_list|)
block|}
decl_stmt|;
name|params
operator|.
name|includeSourcePattern
argument_list|(
name|dirs
argument_list|,
literal|"**/*.java"
argument_list|)
expr_stmt|;
name|JamService
name|jam
init|=
name|jamServiceFactory
operator|.
name|createService
argument_list|(
name|params
argument_list|)
decl_stmt|;
block|{
name|JavaMarshallingGenerator
name|script
init|=
operator|new
name|JavaMarshallingGenerator
argument_list|()
decl_stmt|;
name|script
operator|.
name|setJam
argument_list|(
name|jam
argument_list|)
expr_stmt|;
name|script
operator|.
name|setTargetDir
argument_list|(
name|basedir
operator|+
literal|"/src/main/java"
argument_list|)
expr_stmt|;
name|script
operator|.
name|setOpenwireVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|{
name|JavaTestsGenerator
name|script
init|=
operator|new
name|JavaTestsGenerator
argument_list|()
decl_stmt|;
name|script
operator|.
name|setJam
argument_list|(
name|jam
argument_list|)
expr_stmt|;
name|script
operator|.
name|setTargetDir
argument_list|(
name|basedir
operator|+
literal|"/src/test/java"
argument_list|)
expr_stmt|;
name|script
operator|.
name|setOpenwireVersion
argument_list|(
name|version
argument_list|)
expr_stmt|;
name|script
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|int
name|getVersion
parameter_list|()
block|{
return|return
name|version
return|;
block|}
specifier|public
name|void
name|setVersion
parameter_list|(
name|int
name|version
parameter_list|)
block|{
name|this
operator|.
name|version
operator|=
name|version
expr_stmt|;
block|}
specifier|public
name|File
name|getBasedir
parameter_list|()
block|{
return|return
name|basedir
return|;
block|}
specifier|public
name|void
name|setBasedir
parameter_list|(
name|File
name|basedir
parameter_list|)
block|{
name|this
operator|.
name|basedir
operator|=
name|basedir
expr_stmt|;
block|}
block|}
end_class

end_unit

