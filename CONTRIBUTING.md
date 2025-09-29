# 🤝 Contributing to Kindle TTS Reader

First off, thank you for considering contributing to Kindle TTS Reader! It's people like you that make this project great.

## 📋 Table of Contents

- [Code of Conduct](#code-of-conduct)
- [How Can I Contribute?](#how-can-i-contribute)
- [Getting Started](#getting-started)
- [Development Setup](#development-setup)
- [Submitting Changes](#submitting-changes)
- [Style Guidelines](#style-guidelines)
- [Community](#community)

---

## Code of Conduct

This project and everyone participating in it is governed by our commitment to creating a welcoming and inclusive environment. Please be respectful and constructive in all interactions.

---

## How Can I Contribute?

### 🐛 Reporting Bugs

Before creating bug reports, please check existing issues to avoid duplicates. When you create a bug report, please include:

- **Clear title** describing the issue
- **Detailed description** of the problem
- **Steps to reproduce** the behavior
- **Expected behavior** vs actual behavior
- **Device information** (Android version, device model)
- **Screenshots or logs** if applicable

### 💡 Suggesting Features

Feature suggestions are welcome! Please:

- Check existing feature requests first
- Provide a clear description of the feature
- Explain why this feature would be useful
- Consider implementation complexity

### 🔧 Code Contributions

We welcome code contributions in these areas:

#### **🌍 Localization**
- Add support for new languages
- Improve existing translations
- Update UI text for better UX

#### **🎨 UI/UX Improvements**
- Enhance visual design
- Improve accessibility
- Optimize user workflows

#### **⚡ Performance Optimization**
- OCR accuracy improvements
- Memory usage optimization
- Battery efficiency enhancements

#### **🆕 New Features**
- ePub format support
- Custom TTS voices
- Reading statistics
- Cloud backup integration

#### **🐛 Bug Fixes**
- Fix reported issues
- Improve error handling
- Enhance stability

---

## Getting Started

### Prerequisites

- **Android Studio** Arctic Fox or later
- **JDK 17** or higher
- **Android SDK** API 21 (Android 5.0) minimum
- **Git** for version control

### Fork and Clone

1. Fork the repository on GitHub
2. Clone your fork locally:
```bash
git clone https://github.com/yourusername/kindle-tts-reader.git
cd kindle-tts-reader
```

---

## Development Setup

### 1. Environment Setup

```bash
# Verify Java version
java -version  # Should be 17+

# Open project in Android Studio
# File → Open → Select kindle-tts-reader folder
```

### 2. Build the Project

```bash
# Clean and build
./gradlew clean assembleDebug

# Run tests
./gradlew test

# Run lint
./gradlew lintDebug
```

### 3. Device Setup for Testing

```bash
# Enable developer options on your Android device
# Settings → About → Build number (tap 7 times)

# Enable USB debugging
# Settings → Developer options → USB debugging

# Verify connection
adb devices
```

---

## Submitting Changes

### Branch Naming Convention

- `feature/your-feature-name` - New features
- `bugfix/issue-description` - Bug fixes
- `improvement/what-you-improved` - Enhancements
- `docs/documentation-update` - Documentation only

### Commit Message Format

```
type(scope): short description

Longer description if needed

Fixes #123
```

**Types:**
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation
- `style`: Formatting changes
- `refactor`: Code restructuring
- `test`: Adding tests
- `chore`: Maintenance tasks

### Pull Request Process

1. **Create a branch** from `main`
2. **Make your changes** following style guidelines
3. **Add/update tests** as needed
4. **Update documentation** if applicable
5. **Run the test suite** and ensure it passes
6. **Run lint** and fix any issues
7. **Commit your changes** with clear messages
8. **Push to your fork** and create a Pull Request

### Pull Request Template

```markdown
## Description
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Documentation update
- [ ] Performance improvement

## Testing
- [ ] Tested on emulator
- [ ] Tested on real device
- [ ] Unit tests pass
- [ ] Lint checks pass

## Screenshots
If applicable, add screenshots

## Related Issues
Closes #123
```

---

## Style Guidelines

### Kotlin Code Style

- **Follow Android Kotlin Style Guide**
- **Use 4 spaces** for indentation
- **Maximum line length**: 120 characters
- **Use meaningful variable names**
- **Add KDoc comments** for public APIs

```kotlin
/**
 * Processes OCR text recognition from captured screen
 *
 * @param image The captured screen image
 * @return Recognized text or null if recognition fails
 */
private fun processOCR(image: Image): String? {
    // Implementation
}
```

### XML Resources

- **Use meaningful resource names**
- **Follow Android naming conventions**
- **Group related resources**
- **Add comments for complex layouts**

```xml
<!-- Main activity layout with overlay controls -->
<LinearLayout
    android:id="@+id/layout_main_container"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
```

### Git Commits

- **Use present tense** ("Add feature" not "Added feature")
- **Use imperative mood** ("Move cursor to..." not "Moves cursor to...")
- **Limit first line** to 72 characters
- **Reference issues** when applicable

---

## Testing Guidelines

### Required Tests

- **Unit tests** for core logic
- **Integration tests** for services
- **UI tests** for critical user flows

### Running Tests

```bash
# Unit tests
./gradlew test

# Integration tests
./gradlew connectedAndroidTest

# Specific test class
./gradlew test --tests="OCRServiceTest"
```

### Test Coverage

Aim for **80%+ test coverage** on new code:

```bash
# Generate coverage report
./gradlew jacocoTestReport
```

---

## Documentation

### Code Documentation

- **KDoc comments** for public APIs
- **Inline comments** for complex logic
- **README updates** for new features
- **Architecture documentation** for major changes

### User Documentation

- **Update setup guides** if installation changes
- **Add troubleshooting steps** for new issues
- **Include screenshots** for UI changes
- **Maintain FAQ** with common questions

---

## Community

### Getting Help

- **GitHub Discussions** for questions and ideas
- **GitHub Issues** for bugs and feature requests
- **Code reviews** for learning and improvement

### Recognition

Contributors will be recognized in:
- **README.md** contributor section
- **Release notes** for significant contributions
- **Special thanks** for major features

---

## Development Workflow

### Typical Development Cycle

1. **Choose an issue** from the backlog
2. **Discuss approach** in issue comments
3. **Create feature branch**
4. **Implement changes** with tests
5. **Submit pull request**
6. **Address review feedback**
7. **Merge when approved**

### Code Review Process

- **All changes require review** by maintainers
- **Address feedback constructively**
- **Update PR** based on suggestions
- **Maintain discussion** in PR comments

---

## Release Process

### Versioning

We follow [Semantic Versioning](https://semver.org/):
- **MAJOR**: Breaking changes
- **MINOR**: New features
- **PATCH**: Bug fixes

### Release Checklist

- [ ] All tests passing
- [ ] Documentation updated
- [ ] Changelog updated
- [ ] APK built and tested
- [ ] GitHub release created

---

## Questions?

Don't hesitate to ask! You can:

- **Open an issue** for questions about the codebase
- **Start a discussion** for general questions
- **Comment on existing issues** to join conversations

**Thank you for contributing to Kindle TTS Reader!** 🎉

---

*This project is licensed under the MIT License - see [LICENSE](LICENSE) for details.*