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
name|util
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNull
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertTrue
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|util
operator|.
name|URISupport
operator|.
name|CompositeData
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_class
specifier|public
class|class
name|URISupportTest
block|{
annotation|@
name|Test
specifier|public
name|void
name|testEmptyCompositePath
parameter_list|()
throws|throws
name|Exception
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker:()/localhost?persistent=false"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompositePath
parameter_list|()
throws|throws
name|Exception
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test:(path)/path"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"path"
argument_list|,
name|data
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test:path"
argument_list|)
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|data
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleComposite
parameter_list|()
throws|throws
name|Exception
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test:part1"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testComposite
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"test:(part1://host,part2://(sub1://part,sube2:part))"
argument_list|)
decl_stmt|;
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testEmptyCompositeWithParenthesisInParam
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"failover://()?updateURIsURL=file:/C:/Dir(1)/a.csv"
argument_list|)
decl_stmt|;
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|data
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|data
operator|.
name|getParameters
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"updateURIsURL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"file:/C:/Dir(1)/a.csv"
argument_list|,
name|data
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
literal|"updateURIsURL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompositeWithParenthesisInParam
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"failover://(test)?updateURIsURL=file:/C:/Dir(1)/a.csv"
argument_list|)
decl_stmt|;
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|data
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|data
operator|.
name|getParameters
argument_list|()
operator|.
name|containsKey
argument_list|(
literal|"updateURIsURL"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"file:/C:/Dir(1)/a.csv"
argument_list|,
name|data
operator|.
name|getParameters
argument_list|()
operator|.
name|get
argument_list|(
literal|"updateURIsURL"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompositeWithComponentParam
parameter_list|()
throws|throws
name|Exception
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test:(part1://host?part1=true)?outside=true"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|data
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|part1Params
init|=
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|data
operator|.
name|getComponents
argument_list|()
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|part1Params
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|part1Params
operator|.
name|containsKey
argument_list|(
literal|"part1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParsingURI
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|source
init|=
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:61626/foo/bar?cheese=Edam&x=123"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
init|=
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Size: "
operator|+
name|map
argument_list|,
literal|2
argument_list|,
name|map
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertMapKey
argument_list|(
name|map
argument_list|,
literal|"cheese"
argument_list|,
literal|"Edam"
argument_list|)
expr_stmt|;
name|assertMapKey
argument_list|(
name|map
argument_list|,
literal|"x"
argument_list|,
literal|"123"
argument_list|)
expr_stmt|;
name|URI
name|result
init|=
name|URISupport
operator|.
name|removeQuery
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"result"
argument_list|,
operator|new
name|URI
argument_list|(
literal|"tcp://localhost:61626/foo/bar"
argument_list|)
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|assertMapKey
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
parameter_list|,
name|String
name|key
parameter_list|,
name|Object
name|expected
parameter_list|)
block|{
name|assertEquals
argument_list|(
literal|"Map key: "
operator|+
name|key
argument_list|,
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
argument_list|,
name|expected
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParsingCompositeURI
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|CompositeData
name|data
init|=
name|URISupport
operator|.
name|parseComposite
argument_list|(
operator|new
name|URI
argument_list|(
literal|"broker://(tcp://localhost:61616)?name=foo"
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"one component"
argument_list|,
literal|1
argument_list|,
name|data
operator|.
name|getComponents
argument_list|()
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Size: "
operator|+
name|data
operator|.
name|getParameters
argument_list|()
argument_list|,
literal|1
argument_list|,
name|data
operator|.
name|getParameters
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCheckParenthesis
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|str
init|=
literal|"fred:(((ddd))"
decl_stmt|;
name|assertFalse
argument_list|(
name|URISupport
operator|.
name|checkParenthesis
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
name|str
operator|+=
literal|")"
expr_stmt|;
name|assertTrue
argument_list|(
name|URISupport
operator|.
name|checkParenthesis
argument_list|(
name|str
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCreateWithQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|source
init|=
operator|new
name|URI
argument_list|(
literal|"vm://localhost"
argument_list|)
decl_stmt|;
name|URI
name|dest
init|=
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|source
argument_list|,
literal|"network=true&one=two"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"correct param count"
argument_list|,
literal|2
argument_list|,
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|dest
argument_list|)
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"same uri, host"
argument_list|,
name|source
operator|.
name|getHost
argument_list|()
argument_list|,
name|dest
operator|.
name|getHost
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"same uri, scheme"
argument_list|,
name|source
operator|.
name|getScheme
argument_list|()
argument_list|,
name|dest
operator|.
name|getScheme
argument_list|()
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
literal|"same uri, ssp"
argument_list|,
name|dest
operator|.
name|getQuery
argument_list|()
operator|.
name|equals
argument_list|(
name|source
operator|.
name|getQuery
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testParsingParams
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"static:(http://localhost:61617?proxyHost=jo&proxyPort=90)?proxyHost=localhost&proxyPort=80"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
init|=
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|verifyParams
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
name|uri
operator|=
operator|new
name|URI
argument_list|(
literal|"static://http://localhost:61617?proxyHost=localhost&proxyPort=80"
argument_list|)
expr_stmt|;
name|parameters
operator|=
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|verifyParams
argument_list|(
name|parameters
argument_list|)
expr_stmt|;
name|uri
operator|=
operator|new
name|URI
argument_list|(
literal|"http://0.0.0.0:61616"
argument_list|)
expr_stmt|;
name|parameters
operator|=
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|uri
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testCompositeCreateURIWithQuery
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryString
init|=
literal|"query=value"
decl_stmt|;
name|URI
name|originalURI
init|=
operator|new
name|URI
argument_list|(
literal|"outerscheme:(innerscheme:innerssp)"
argument_list|)
decl_stmt|;
name|URI
name|querylessURI
init|=
name|originalURI
decl_stmt|;
name|assertEquals
argument_list|(
name|querylessURI
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|querylessURI
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
name|querylessURI
operator|+
literal|"?"
operator|+
name|queryString
argument_list|)
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
name|queryString
argument_list|)
argument_list|)
expr_stmt|;
name|originalURI
operator|=
operator|new
name|URI
argument_list|(
literal|"outerscheme:(innerscheme:innerssp)?outerquery=0"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|querylessURI
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|querylessURI
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
name|querylessURI
operator|+
literal|"?"
operator|+
name|queryString
argument_list|)
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
name|queryString
argument_list|)
argument_list|)
expr_stmt|;
name|originalURI
operator|=
operator|new
name|URI
argument_list|(
literal|"outerscheme:(innerscheme:innerssp?innerquery=0)"
argument_list|)
expr_stmt|;
name|querylessURI
operator|=
name|originalURI
expr_stmt|;
name|assertEquals
argument_list|(
name|querylessURI
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|querylessURI
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
name|querylessURI
operator|+
literal|"?"
operator|+
name|queryString
argument_list|)
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
name|queryString
argument_list|)
argument_list|)
expr_stmt|;
name|originalURI
operator|=
operator|new
name|URI
argument_list|(
literal|"outerscheme:(innerscheme:innerssp?innerquery=0)?outerquery=0"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|querylessURI
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|querylessURI
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
operator|new
name|URI
argument_list|(
name|querylessURI
operator|+
literal|"?"
operator|+
name|queryString
argument_list|)
argument_list|,
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|originalURI
argument_list|,
name|queryString
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testApplyParameters
parameter_list|()
throws|throws
name|Exception
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
literal|"http://0.0.0.0:61616"
argument_list|)
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
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
name|parameters
operator|.
name|put
argument_list|(
literal|"t.proxyHost"
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|parameters
operator|.
name|put
argument_list|(
literal|"t.proxyPort"
argument_list|,
literal|"80"
argument_list|)
expr_stmt|;
name|uri
operator|=
name|URISupport
operator|.
name|applyParameters
argument_list|(
name|uri
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|appliedParameters
init|=
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"all params applied  with no prefix"
argument_list|,
literal|2
argument_list|,
name|appliedParameters
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
comment|// strip off params again
name|uri
operator|=
name|URISupport
operator|.
name|createURIWithQuery
argument_list|(
name|uri
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|uri
operator|=
name|URISupport
operator|.
name|applyParameters
argument_list|(
name|uri
argument_list|,
name|parameters
argument_list|,
literal|"joe"
argument_list|)
expr_stmt|;
name|appliedParameters
operator|=
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"no params applied as none match joe"
argument_list|,
name|appliedParameters
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|uri
operator|=
name|URISupport
operator|.
name|applyParameters
argument_list|(
name|uri
argument_list|,
name|parameters
argument_list|,
literal|"t."
argument_list|)
expr_stmt|;
name|verifyParams
argument_list|(
name|URISupport
operator|.
name|parseParameters
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|verifyParams
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parameters
parameter_list|)
block|{
name|assertEquals
argument_list|(
name|parameters
operator|.
name|get
argument_list|(
literal|"proxyHost"
argument_list|)
argument_list|,
literal|"localhost"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|parameters
operator|.
name|get
argument_list|(
literal|"proxyPort"
argument_list|)
argument_list|,
literal|"80"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsCompositeURIWithQueryNoSlashes
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|URI
index|[]
name|compositeURIs
init|=
operator|new
name|URI
index|[]
block|{
operator|new
name|URI
argument_list|(
literal|"test:(part1://host?part1=true)?outside=true"
argument_list|)
block|,
operator|new
name|URI
argument_list|(
literal|"broker:(tcp://localhost:61616)?name=foo"
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|URI
name|uri
range|:
name|compositeURIs
control|)
block|{
name|assertTrue
argument_list|(
name|uri
operator|+
literal|" must be detected as composite URI"
argument_list|,
name|URISupport
operator|.
name|isCompositeURI
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsCompositeURIWithQueryAndSlashes
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|URI
index|[]
name|compositeURIs
init|=
operator|new
name|URI
index|[]
block|{
operator|new
name|URI
argument_list|(
literal|"test://(part1://host?part1=true)?outside=true"
argument_list|)
block|,
operator|new
name|URI
argument_list|(
literal|"broker://(tcp://localhost:61616)?name=foo"
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|URI
name|uri
range|:
name|compositeURIs
control|)
block|{
name|assertTrue
argument_list|(
name|uri
operator|+
literal|" must be detected as composite URI"
argument_list|,
name|URISupport
operator|.
name|isCompositeURI
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsCompositeURINoQueryNoSlashes
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|URI
index|[]
name|compositeURIs
init|=
operator|new
name|URI
index|[]
block|{
operator|new
name|URI
argument_list|(
literal|"test:(part1://host,part2://(sub1://part,sube2:part))"
argument_list|)
block|,
operator|new
name|URI
argument_list|(
literal|"test:(path)/path"
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|URI
name|uri
range|:
name|compositeURIs
control|)
block|{
name|assertTrue
argument_list|(
name|uri
operator|+
literal|" must be detected as composite URI"
argument_list|,
name|URISupport
operator|.
name|isCompositeURI
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsCompositeURINoQueryNoSlashesNoParentheses
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|assertFalse
argument_list|(
literal|"test:part1"
operator|+
literal|" must be detected as non-composite URI"
argument_list|,
name|URISupport
operator|.
name|isCompositeURI
argument_list|(
operator|new
name|URI
argument_list|(
literal|"test:part1"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testIsCompositeURINoQueryWithSlashes
parameter_list|()
throws|throws
name|URISyntaxException
block|{
name|URI
index|[]
name|compositeURIs
init|=
operator|new
name|URI
index|[]
block|{
operator|new
name|URI
argument_list|(
literal|"failover://(tcp://bla:61616,tcp://bla:61617)"
argument_list|)
block|,
operator|new
name|URI
argument_list|(
literal|"failover://(tcp://localhost:61616,ssl://anotherhost:61617)"
argument_list|)
block|}
decl_stmt|;
for|for
control|(
name|URI
name|uri
range|:
name|compositeURIs
control|)
block|{
name|assertTrue
argument_list|(
name|uri
operator|+
literal|" must be detected as composite URI"
argument_list|,
name|URISupport
operator|.
name|isCompositeURI
argument_list|(
name|uri
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

