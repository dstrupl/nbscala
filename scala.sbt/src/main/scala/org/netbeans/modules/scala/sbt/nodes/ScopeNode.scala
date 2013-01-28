package org.netbeans.modules.scala.sbt.nodes

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener
import javax.swing.SwingUtilities
import org.netbeans.api.project.Project
import org.netbeans.modules.scala.sbt.classpath.SBTResourceController
import org.openide.filesystems.FileUtil
import org.openide.nodes.Children
import org.openide.nodes.Node
import org.openide.util.NbBundle

class ScopeNode(project: Project, scope: String) extends AbstractFolderNode(new ScopesChildren(project, scope)) {

  override
  def getDisplayName = NbBundle.getMessage(classOf[ScopeNode], "CTL_Scope_" + scope)

  override
  def getName = scope

  override
  protected def getBadge = Icons.ICON_LIBARARIES_BADGE
}

private class ScopesChildren(project: Project, scope: String) extends Children.Keys[ArtifactInfo] with PropertyChangeListener {

  setKeys

  override
  protected def createNodes(key: ArtifactInfo): Array[Node] = {
    Array(new ArtifactNode(key, project))
  }

  def propertyChange(evt: PropertyChangeEvent) {
    if (SBTResourceController.SBT_LIBRARY_RESOLVED == evt.getPropertyName) {
      setKeys
    }
  }

  private def setKeys {
    val sbtController = project.getLookup.lookup(classOf[SBTResourceController])
    val artifacts = sbtController.getResolvedLibraries(scope) filter (FileUtil.isArchiveFile(_)) map {fo =>
      ArtifactInfo(fo.getNameExt, "", "", FileUtil.toFile(fo), null, null)
    }
    
    SwingUtilities.invokeLater(new Runnable() {
        def run() {
          setKeys(artifacts.sortBy(_.name))
        }
      })
  }
}