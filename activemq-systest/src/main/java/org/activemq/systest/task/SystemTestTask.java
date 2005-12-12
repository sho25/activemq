begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
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
name|DirectoryScanner
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
name|taskdefs
operator|.
name|MatchingTask
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
name|types
operator|.
name|FileSet
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
name|types
operator|.
name|Path
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
name|types
operator|.
name|Reference
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_comment
comment|/**  * An Ant task for executing Gram scripts, which are Groovy scripts executed on  * the JAM context.  *   * @version $Revision: 1.2 $  */
end_comment

begin_class
specifier|public
class|class
name|SystemTestTask
extends|extends
name|MatchingTask
block|{
specifier|private
specifier|static
specifier|final
name|String
name|SCENARIOS_PROPERTIES_FILE
init|=
literal|"activemq-scenarios.properties"
decl_stmt|;
specifier|private
name|Path
name|srcDir
init|=
literal|null
decl_stmt|;
specifier|private
name|Path
name|mToolpath
init|=
literal|null
decl_stmt|;
specifier|private
name|Path
name|mClasspath
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|mIncludes
init|=
literal|"**/*.java"
decl_stmt|;
specifier|private
name|File
name|destDir
decl_stmt|;
specifier|private
name|FileSet
name|clientFiles
decl_stmt|;
specifier|private
name|FileSet
name|brokerFiles
decl_stmt|;
specifier|private
name|File
name|scenariosFile
decl_stmt|;
specifier|public
name|File
name|getScenariosFile
parameter_list|()
block|{
return|return
name|scenariosFile
return|;
block|}
specifier|public
name|void
name|setScenariosFile
parameter_list|(
name|File
name|scenariosFile
parameter_list|)
block|{
name|this
operator|.
name|scenariosFile
operator|=
name|scenariosFile
expr_stmt|;
block|}
comment|/**      * Sets the directory into which source files should be generated.      */
specifier|public
name|void
name|setDestDir
parameter_list|(
name|File
name|destDir
parameter_list|)
block|{
name|this
operator|.
name|destDir
operator|=
name|destDir
expr_stmt|;
block|}
specifier|public
name|void
name|setSrcDir
parameter_list|(
name|Path
name|srcDir
parameter_list|)
block|{
name|this
operator|.
name|srcDir
operator|=
name|srcDir
expr_stmt|;
block|}
specifier|public
name|void
name|setToolpath
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
if|if
condition|(
name|mToolpath
operator|==
literal|null
condition|)
block|{
name|mToolpath
operator|=
name|path
expr_stmt|;
block|}
else|else
block|{
name|mToolpath
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setToolpathRef
parameter_list|(
name|Reference
name|r
parameter_list|)
block|{
name|createToolpath
argument_list|()
operator|.
name|setRefid
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
specifier|public
name|FileSet
name|createBrokerFiles
parameter_list|()
block|{
return|return
operator|new
name|FileSet
argument_list|()
return|;
block|}
specifier|public
name|FileSet
name|getBrokerFiles
parameter_list|()
block|{
return|return
name|brokerFiles
return|;
block|}
specifier|public
name|void
name|setBrokerFiles
parameter_list|(
name|FileSet
name|brokerFiles
parameter_list|)
block|{
name|this
operator|.
name|brokerFiles
operator|=
name|brokerFiles
expr_stmt|;
block|}
specifier|public
name|FileSet
name|createClientFiles
parameter_list|()
block|{
return|return
operator|new
name|FileSet
argument_list|()
return|;
block|}
specifier|public
name|FileSet
name|getClientFiles
parameter_list|()
block|{
return|return
name|clientFiles
return|;
block|}
specifier|public
name|void
name|setClientFiles
parameter_list|(
name|FileSet
name|clientFiles
parameter_list|)
block|{
name|this
operator|.
name|clientFiles
operator|=
name|clientFiles
expr_stmt|;
block|}
specifier|public
name|Path
name|createToolpath
parameter_list|()
block|{
if|if
condition|(
name|mToolpath
operator|==
literal|null
condition|)
block|{
name|mToolpath
operator|=
operator|new
name|Path
argument_list|(
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|mToolpath
operator|.
name|createPath
argument_list|()
return|;
block|}
specifier|public
name|void
name|setClasspath
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
if|if
condition|(
name|mClasspath
operator|==
literal|null
condition|)
block|{
name|mClasspath
operator|=
name|path
expr_stmt|;
block|}
else|else
block|{
name|mClasspath
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setClasspathRef
parameter_list|(
name|Reference
name|r
parameter_list|)
block|{
name|createClasspath
argument_list|()
operator|.
name|setRefid
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Path
name|createClasspath
parameter_list|()
block|{
if|if
condition|(
name|mClasspath
operator|==
literal|null
condition|)
block|{
name|mClasspath
operator|=
operator|new
name|Path
argument_list|(
name|getProject
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|mClasspath
operator|.
name|createPath
argument_list|()
return|;
block|}
specifier|public
name|void
name|execute
parameter_list|()
throws|throws
name|BuildException
block|{
comment|/*          * if (srcDir == null) { throw new BuildException("'srcDir' must be          * specified"); }          */
if|if
condition|(
name|scenariosFile
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"'scenariosFile' must be specified"
argument_list|)
throw|;
block|}
if|if
condition|(
name|destDir
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"'destDir' must be specified"
argument_list|)
throw|;
block|}
if|if
condition|(
name|clientFiles
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"'clientFiles' must be specified"
argument_list|)
throw|;
block|}
if|if
condition|(
name|brokerFiles
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|BuildException
argument_list|(
literal|"'clientFiles' must be specified"
argument_list|)
throw|;
block|}
name|JamServiceFactory
name|jamServiceFactory
init|=
name|JamServiceFactory
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|JamServiceParams
name|serviceParams
init|=
name|jamServiceFactory
operator|.
name|createServiceParams
argument_list|()
decl_stmt|;
if|if
condition|(
name|mToolpath
operator|!=
literal|null
condition|)
block|{
name|File
index|[]
name|tcp
init|=
name|path2files
argument_list|(
name|mToolpath
argument_list|)
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
name|tcp
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|serviceParams
operator|.
name|addToolClasspath
argument_list|(
name|tcp
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|mClasspath
operator|!=
literal|null
condition|)
block|{
name|File
index|[]
name|cp
init|=
name|path2files
argument_list|(
name|mClasspath
argument_list|)
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
name|cp
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|serviceParams
operator|.
name|addClasspath
argument_list|(
name|cp
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|JClass
index|[]
name|classes
init|=
literal|null
decl_stmt|;
name|File
name|propertiesFile
init|=
name|scenariosFile
decl_stmt|;
try|try
block|{
if|if
condition|(
name|srcDir
operator|!=
literal|null
condition|)
block|{
name|serviceParams
operator|.
name|includeSourcePattern
argument_list|(
name|path2files
argument_list|(
name|srcDir
argument_list|)
argument_list|,
name|mIncludes
argument_list|)
expr_stmt|;
name|JamService
name|jam
init|=
name|jamServiceFactory
operator|.
name|createService
argument_list|(
name|serviceParams
argument_list|)
decl_stmt|;
name|classes
operator|=
name|jam
operator|.
name|getAllClasses
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// lets try load the properties file
name|classes
operator|=
name|loadScenarioClasses
argument_list|()
expr_stmt|;
name|propertiesFile
operator|=
literal|null
expr_stmt|;
block|}
name|DirectoryScanner
name|clientsScanner
init|=
name|clientFiles
operator|.
name|getDirectoryScanner
argument_list|(
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|DirectoryScanner
name|brokersScanner
init|=
name|brokerFiles
operator|.
name|getDirectoryScanner
argument_list|(
name|getProject
argument_list|()
argument_list|)
decl_stmt|;
name|SystemTestGenerator
name|generator
init|=
operator|new
name|SystemTestGenerator
argument_list|(
name|classes
argument_list|,
name|destDir
argument_list|,
name|clientsScanner
argument_list|,
name|brokersScanner
argument_list|,
name|getProject
argument_list|()
operator|.
name|getBaseDir
argument_list|()
argument_list|,
name|propertiesFile
argument_list|)
decl_stmt|;
name|generator
operator|.
name|generate
argument_list|()
expr_stmt|;
name|log
argument_list|(
literal|"...done."
argument_list|)
expr_stmt|;
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
specifier|protected
name|JClass
index|[]
name|loadScenarioClasses
parameter_list|()
throws|throws
name|IOException
block|{
name|InputStream
name|in
init|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
name|SCENARIOS_PROPERTIES_FILE
argument_list|)
decl_stmt|;
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
name|ClassLoader
name|contextClassLoader
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
if|if
condition|(
name|contextClassLoader
operator|!=
literal|null
condition|)
block|{
name|in
operator|=
name|contextClassLoader
operator|.
name|getResourceAsStream
argument_list|(
name|SCENARIOS_PROPERTIES_FILE
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|in
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not find ActiveMQ scenarios properties file on the classpath: "
operator|+
name|SCENARIOS_PROPERTIES_FILE
argument_list|)
throw|;
block|}
block|}
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|properties
operator|.
name|load
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|properties
operator|.
name|entrySet
argument_list|()
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
name|Map
operator|.
name|Entry
name|entry
init|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|className
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|String
name|names
init|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|String
index|[]
name|interfaceNameArray
init|=
name|parseNames
argument_list|(
name|names
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
operator|new
name|ScenarioJClassStub
argument_list|(
name|className
argument_list|,
name|interfaceNameArray
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|JClass
index|[]
name|answer
init|=
operator|new
name|JClass
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|list
operator|.
name|toArray
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|String
index|[]
name|parseNames
parameter_list|(
name|String
name|names
parameter_list|)
block|{
name|StringTokenizer
name|iter
init|=
operator|new
name|StringTokenizer
argument_list|(
name|names
argument_list|)
decl_stmt|;
name|List
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|iter
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|text
init|=
name|iter
operator|.
name|nextToken
argument_list|()
operator|.
name|trim
argument_list|()
decl_stmt|;
if|if
condition|(
name|text
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|list
operator|.
name|add
argument_list|(
name|text
argument_list|)
expr_stmt|;
block|}
block|}
name|String
index|[]
name|answer
init|=
operator|new
name|String
index|[
name|list
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|list
operator|.
name|toArray
argument_list|(
name|answer
argument_list|)
expr_stmt|;
return|return
name|answer
return|;
block|}
specifier|protected
name|File
index|[]
name|path2files
parameter_list|(
name|Path
name|path
parameter_list|)
block|{
name|String
index|[]
name|list
init|=
name|path
operator|.
name|list
argument_list|()
decl_stmt|;
name|File
index|[]
name|out
init|=
operator|new
name|File
index|[
name|list
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
name|out
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|out
index|[
name|i
index|]
operator|=
operator|new
name|File
argument_list|(
name|list
index|[
name|i
index|]
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
block|}
return|return
name|out
return|;
block|}
block|}
end_class

end_unit

